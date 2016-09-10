package com.taotao.sso.service;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.sso.mapper.UserMapper;
import com.taotao.sso.pojo.User;

@Service
public class UserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Boolean check(String param, Integer type) {
        if (type < 1 || type > 3) {
            return null;
        }
        User record = new User();
        switch (type) {
        case 1:
            record.setUsername(param);
            break;
        case 2:
            record.setPhone(param);
            break;
        case 3:
            record.setEmail(param);
            break;
        default:
            break;
        }
        return this.userMapper.selectOne(record) == null;
    }

    public Boolean saveUser(User user) {
        user.setId(null);
        user.setCreated(new Date());
        user.setUpdated(user.getCreated());

        user.setPassword(DigestUtils.md5Hex(user.getPassword()));

        return this.userMapper.insert(user) == 1;
    }

    public String doLogin(String username, String password) throws Exception {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if (null == user) {
            return null;
        }
     // 登录时，下面的方法2性能更高，因为数据库查询where条件是一个字段要比两个字段的效率高很多
//      方法1：根据用户名和密码查询用户对象
//      方法2：根据用户名查询到用户对象，然后根据用户对象的密码和输入的密码进行判断
      // 密码通过MD5进行加密处理
        // 比对密码是否正确
        if (!StringUtils.equals(DigestUtils.md5Hex(password), user.getPassword())) {
            return null;
        }

        // 登录成功
        // 生存token
        String token = DigestUtils.md5Hex(System.currentTimeMillis() + username);
//        作为缓存和作为数据库的区别
//        在缓存中如果数据丢失，不影响程序运行，数据库中则会影响程序运行，token丢失会影响程序运行所以，token是放在数据库中而非缓存
//        设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码

        // 将用户数据保存到redis中
        this.redisService.set("TOKEN_" + token, MAPPER.writeValueAsString(user), 60 * 30);

        return token;
    }

    public User queryUserByToken(String token) {
        String key = "TOKEN_" + token;
        String jsonData = this.redisService.get(key);
        if (StringUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            // 刷新用户的生存时间(非常重要)
            this.redisService.expire(key, 60 * 30);
            return MAPPER.readValue(jsonData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
