package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {

	@Autowired(required = false)//如果spring容器中有就注入，没有就忽略
	ShardedJedisPool shardedJedisPool;

	public <T> T excute(Function<T, ShardedJedis> fun) {
		ShardedJedis shardedJedis = null;
		try {
			// 从连接池中获取到jedis分片对象
			shardedJedis = shardedJedisPool.getResource();
			return fun.callback(shardedJedis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != shardedJedis) {
				// 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
				shardedJedis.close();
			}
		}
		return null;
	}
	
	public Boolean exist(final String key) {
            return this.excute(new Function<Boolean, ShardedJedis>() {

                    @Override
                    public Boolean callback(ShardedJedis e) {
                            return e.exists(key);
                    }
            });
    }

	public String set(final String key, final String value) {
		return this.excute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value);
			}
		});
	}

	public String get(final String key) {
		return this.excute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				return e.get(key);
			}
		});
	}
	
	public Long del(final String key) {
		return this.excute(new Function<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key);
			}
		});
	}
	
	public Long expire(final String key, final Integer seconds) {
		return this.excute(new Function<Long, ShardedJedis>() {

			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key, seconds);
			}
		});
	}
	
	public String set(final String key, final String value, final Integer seconds) {
		return this.excute(new Function<String, ShardedJedis>() {

			@Override
			public String callback(ShardedJedis e) {
				String result = e.set(key, value);
				e.expire(key, seconds);
				return result;
			}
		});
	}
}
