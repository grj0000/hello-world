package com.taotao.web.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.Cart;

@Service
public class CartService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_CART_URL}")
    private String TAOTAO_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<Cart> queryCartListByUserId(Long userId) {
        // 查询购物车系统提供的接口获取购物车列表
        try {
            String url = TAOTAO_CART_URL + "/service/cart?userId=" + userId;//避免和购物车系统的已有查询购物车列表的addItemToCart方法url冲突，又要遵守restful风格
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            return MAPPER.readValue(jsonData,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
