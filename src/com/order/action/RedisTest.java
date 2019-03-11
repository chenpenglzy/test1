package com.order.action;

import com.order.util.RedisUtil;

import redis.clients.jedis.Jedis;

public class RedisTest {

	public static void main(String[] args) {
        // TODO Auto-generated method stub
        //连接redis服务
        Jedis jedis = RedisUtil.getJedis();
        //密码验证-如果你没有设置redis密码可不验证即可使用相关命令
        //jedis.auth("abcdefg");
        //简单的key-value 存储
        jedis.set("redis2", "myredis2");
        System.out.println(jedis.get("redis21"));
    }
}
