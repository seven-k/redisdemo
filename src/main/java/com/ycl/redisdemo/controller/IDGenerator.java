package com.ycl.redisdemo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
        return "hostName" + i;

    }

    private synchronized String saveHostAndGenerateId(String hostName, int stepNum) {
        String year = String.valueOf(LocalDateTime.now().getYear()).substring(2);
        long[] initValueArray = {
                Long.valueOf(year + "000000001"), Long.valueOf(year + "000000002"), Long.valueOf(year + "000000003"),
                Long.valueOf(year + "000000004"), Long.valueOf(year + "000000005"), Long.valueOf(year + "000000006"),
                Long.valueOf(year + "000000007"), Long.valueOf(year + "000000008"), Long.valueOf(year + "000000009")
        };
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys("test:hostname*");
        if (keys != null) {
            for (String key : keys) {
                if (("test:hostname:" + hostName).equals(key)) {  //已存在
                    String initValue = (String) operations.get(key);
                    long incrCount = operations.increment(hostName + "_incr_count", stepNum);
                    long param = Long.valueOf(initValue) + incrCount;
                    System.out.println(param);
                    return "" + param;
                }

            }
            int hostCount = keys.size();
            {  //不存在
                long incrCount = operations.increment(hostName + "_incr_count", stepNum);
                long param = initValueArray[hostCount] + incrCount;
                operations.set("test:hostname:" + hostName, String.valueOf(initValueArray[hostCount]));
                System.out.println(param);
                return "" + param;
            }
        } else {//第一个
            long incrCount = operations.increment(hostName + "_incr_count", stepNum);
            long param = initValueArray[0] + incrCount;
            operations.set("test:hostname:" + hostName, String.valueOf(initValueArray[0]));
            System.out.println(param);
            return "" + param;
        }
    }


    @GetMapping("/get")
    public void main() {
        int taskSize = 5;
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
                    saveHostAndGenerateId(getHostName(), 9);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

