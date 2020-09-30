package cn.hzby.whc.util;

import redis.clients.jedis.Jedis;

public class RedisUtil {

    //设置单个机器的钉钉警报时间，没有则设置，有则返回
    public static boolean selectAndSet(String key){
        try (Jedis jedis = new Jedis("localhost",6379)) {
            jedis.auth("");
            if (jedis.get(key+"DDTime") == null || jedis.get(key+"DDTime").isEmpty()) {
                jedis.set(key+"DDTime", "true");
                jedis.expire(key+"DDTime",600);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
