package com.ycl.redisdemo.controller;


import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class IDGenerator {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * just for Test
     */
    private String getHostName() throws UnknownHostException {
        //OsInfoUtils.getLocalHostName();
        int i = (int) (Math.random() * 5);
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        return hostName + i;

    }

    private synchronized String saveHostAndGenerateId(String hostName, int stepNum) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String date = FastDateFormat.getInstance("yyMMddhhmm").format(new Date());
        String timeTag = operations.get("adj_time_tag");
        if (timeTag == null) {
            operations.set("adj_time_tag", date);
        } else {
            if (!date.equals(timeTag)) {
                operations.set("adj_time_tag", date);
                Set<String> keys = redisTemplate.keys("adj:*");
                for (String key : keys) redisTemplate.delete(key);
            }
        }
        long[] initValueArray = {
                Long.valueOf(date + "0000001"), Long.valueOf(date + "0000002"), Long.valueOf(date + "0000003"),
                Long.valueOf(date + "0000004"), Long.valueOf(date + "0000005"), Long.valueOf(date + "0000006"),
                Long.valueOf(date + "0000007"), Long.valueOf(date + "0000008"), Long.valueOf(date + "0000009"),
                Long.valueOf(date + "0000010"), Long.valueOf(date + "0000011"), Long.valueOf(date + "0000012")
        };
        Set<String> keys = redisTemplate.keys("adj:hostname*");
        if (keys != null) {
            for (String key : keys) {
                if (("adj:hostname:" + hostName).equals(key)) {  //已存在
                    String initValue = (String) operations.get(key);
                    long incrCount = operations.increment("adj:"+hostName + ":incrCount", stepNum);
                    long param = Long.valueOf(initValue) + incrCount;
                    System.out.println(param);
                    return "" + param;
                }

            }
            int hostCount = keys.size();
            {  //不存在
                long incrCount = operations.increment("adj:"+hostName + ":incrCount", stepNum);
                long param = initValueArray[hostCount] + incrCount;
                operations.set("adj:hostname:" + hostName, String.valueOf(initValueArray[hostCount]));
                System.out.println(param);
                return "" + param;
            }
        } else {//第一个
            long incrCount = operations.increment("adj:"+hostName + ":incrCount", stepNum);
            long param = initValueArray[0] + incrCount;
            operations.set("adj:hostname:" + hostName, String.valueOf(initValueArray[0]));
            System.out.println(param);
            return "" + param;
        }
    }


    @GetMapping("/get")
    public void main() {
        int taskSize = 30;
        // 创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(taskSize);
        // 创建多个有返回值的任务
        for (int i = 0; i < taskSize; i++) {
            Thread thread = new MyThread();
            thread.start();
        }
        // 关闭线程池
        pool.shutdown();
    }


    private class MyThread extends Thread {
        public void run() {
            for (int i = 0; i < 1000; i++) {
                try {
                    saveHostAndGenerateId(getHostName(), 12);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

