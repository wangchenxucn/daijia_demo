package com.atguigu.daijia.customer.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WxConfigOperator {


    @Autowired
    private WxConfigProperties wxConfigProperties;

    @Bean
    public WxMaService wxMaService() {

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxConfigProperties.getAppId());
        config.setSecret(wxConfigProperties.getAppSecret());

        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);

        return wxMaService;
    }

}
