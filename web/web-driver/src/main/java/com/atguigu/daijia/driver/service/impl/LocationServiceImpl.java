package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationFeignClient locationFeignClient;
    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        //根据司机id查询设置
        Long driverId = updateDriverLocationForm.getDriverId();
        Result<DriverSet> driverSetResult = driverInfoFeignClient.getDriverSet(driverId);
        DriverSet driverSet = driverSetResult.getData();
        //司机状态开始接单 就更新
        if(driverSet.getServiceStatus() == 1){
            Result<Boolean> booleanResult = locationFeignClient.updateDriverLocation(updateDriverLocationForm);
            return booleanResult.getData();
        }
        else {
            throw new RuntimeException(String.valueOf(ResultCodeEnum.NO_START_SERVICE));
        }

    }

    @Override
    public Boolean removeDriverLocation(Long driverId) {
        return locationFeignClient.removeDriverLocation(driverId).getData();
    }
}
