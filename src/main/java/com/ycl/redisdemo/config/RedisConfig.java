package com.ycl.redisdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisConfig extends WebMvcConfigurationSupport {

    @Autowired
    private RedisConfigInfo redisConfigInfo;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(redisConfigInfo.getMinIdle());
        config.setMaxIdle(redisConfigInfo.getMaxIdle());
        config.setMaxWaitMillis(redisConfigInfo.getMaxWait());
        config.setTestOnBorrow(true);
        return config;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        /*JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisConfigInfo.getHost());
        jedisConnectionFactory.setPort(redisConfigInfo.getPort());
        jedisConnectionFactory.setTimeout(redisConfigInfo.getTimeOut());
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        return jedisConnectionFactory;
*/

        RedisStandaloneConfiguration redisStandaloneConfiguration=new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConfigInfo.getHost());
        redisStandaloneConfiguration.setPort(redisConfigInfo.getPort());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        return redisTemplate;
    }
}
