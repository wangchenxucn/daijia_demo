package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
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
        redisTemplate.opsForValue().set(token,result.getData().toString(),24, TimeUnit.HOURS);

        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(String token) {
        //在redis里面查有没有token 查询出对应的用户id
        String customerId = (String) redisTemplate.opsForValue().get(token);
        if (customerId == null) {
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }
        //根据用户id远程调用接口获取用户信息
        Result<CustomerLoginVo> customerLoginVoResult = customerInfoFeignClient.getCustomerLoginInfo(Long.valueOf(customerId));
        if(customerLoginVoResult.getCode()!=200){
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }

        CustomerLoginVo customerLoginVo = customerLoginVoResult.getData();
        return customerLoginVo;
    }

    @Override
    public CustomerLoginVo getCustomerInfo(Long customerId) {

        //根据用户id远程调用接口获取用户信息
        Result<CustomerLoginVo> customerLoginVoResult = customerInfoFeignClient.getCustomerLoginInfo(customerId);
        if(customerLoginVoResult.getCode()!=200){
            throw new GuiguException(ResultCodeEnum.AUTH_ERROR);
        }

        CustomerLoginVo customerLoginVo = customerLoginVoResult.getData();
        return customerLoginVo;
    }
}
