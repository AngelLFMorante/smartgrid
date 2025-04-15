package com.smartgrid.config;

import com.smartgrid.service.MQTTSubscriberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQTTConfig {
    @Bean
    public MQTTSubscriberService mqttSubscriberService() {
        return new MQTTSubscriberService();
    }
}

