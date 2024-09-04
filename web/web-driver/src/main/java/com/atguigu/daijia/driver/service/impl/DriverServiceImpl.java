package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.DriverService;
import com.atguigu.daijia.model.vo.driver.DriverLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {

        Result<Long> longResult = driverInfoFeignClient.login(code);
        Long driverId = longResult.getData();

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token,driverId.toString(),24, TimeUnit.HOURS);


        return token;
    }

    @Override
    public DriverLoginVo getDriverLoginInfo(Long driverId) {

        Result<DriverLoginVo> driverLoginVoResult = driverInfoFeignClient.getDriverLoginInfo(driverId);
        DriverLoginVo driverLoginVo = driverLoginVoResult.getData();

        return driverLoginVo;
    }
}
