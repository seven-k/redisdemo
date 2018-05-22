package com.ycl.redisdemo.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigInfo {
    private String host;
    private Integer port;
    private Integer maxWait;
    private Integer minIdle;
    private Integer maxIdle;
    private Integer timeOut;

    public String getHost() {
        return host;
    }

    public RedisConfigInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public RedisConfigInfo setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public RedisConfigInfo setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
        return this;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public RedisConfigInfo setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public RedisConfigInfo setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public RedisConfigInfo setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
        return this;
    }
}
