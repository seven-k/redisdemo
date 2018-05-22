package com.ycl.redisdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yin Changlei
 * @date 2018/5/22 11:03
 */

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/generateId")
    public Map<String, String> generateId() {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String aaa = operations.get("aaa");
        return Collections.singletonMap("id", aaa);
    }

    public Long generateId(String key, Date date) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expireAt(date);
        return counter.incrementAndGet();
    }

    //组装符合自己规则的id并设置过期时间

    /**
     * @param key       redis中的key值
     * @param prefix    最后编码的前缀
     * @param hasExpire redis是否使用过期时间生成自增id
     * @param minLength redis生成的自增id的最小长度，如果小于这个长度前面补0
     * @return
     */
    private String generateCode(String key, String prefix, boolean hasExpire, Integer minLength) {
        try {
            Date date = null;
            Long id = null;
            if (hasExpire) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                date = calendar.getTime();
            }
            id = generateId(key, date);
            if (id != null) {
                return this.format(id, prefix, date, minLength);
            }
        } catch (Exception e) {
            logger.info("error-->redis生成id时出现异常");
            logger.error(e.getMessage(), e);
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    //设定格式

    private String format(Long id, String prefix, Date date, Integer minLength) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        if (date != null) {
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            sb.append(df.format(date));
        }
        String strId = String.valueOf(id);
        int length = strId.length();
        if (length < minLength) {
            for (int i = 0; i < minLength - length; i++) {
                sb.append("0");
            }
            sb.append(strId);
        } else {
            sb.append(strId);
        }
        return sb.toString();
    }
}
