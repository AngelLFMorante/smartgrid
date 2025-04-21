package com.smartgrid.config;
import com.smartgrid.service.MQTTSubscriberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MQTTConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.client.id}")
    private String clientId;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public String getTopic() {
        return topic;
    }

    public String getClientId() {
        return clientId;
    }

}

