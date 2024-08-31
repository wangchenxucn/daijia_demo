package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {

        //把code远程调用 返回id
        Result<Long> result = customerInfoFeignClient.login(code);
        //如果返回失败 返回错误
        if(result.getCode()!=200){
            //System.out.println(result.getCode());
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //获取用户id 为空返回错误
        if(result.getData()==null){
            throw new GuiguException(ResultCodeEnum.FAIL);
        }
        //通过id生成token字符串
        String token = UUID.randomUUID().toString();
        //放到redis存储 设置过期时间
        redisTemplate.opsForValue().set(token,result.getData(),24, TimeUnit.HOURS);

        return token;
    }
}
