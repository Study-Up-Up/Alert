package cn.hzby.whc.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisSonConfig {

    //自定义的简单配置RedisSon
    @Bean(name = "redissonClient")
    public RedissonClient getRedisSon() throws Exception{
        Config config=new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.useSingleServer().setPassword("");
        return Redisson.create(config);
    }
}
