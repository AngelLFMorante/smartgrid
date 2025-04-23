package com.smartgrid.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class MQTTConfigTest {

    @Test
    public void testGetterMethods() {
        MQTTConfig config = new MQTTConfig();

        ReflectionTestUtils.setField(config, "brokerUrl", "tcp://localhost:1883");
        ReflectionTestUtils.setField(config, "topic", "smartgrid/consumption");
        ReflectionTestUtils.setField(config, "clientId", "smartgrid-client");

        assertEquals("tcp://localhost:1883", config.getBrokerUrl());
        assertEquals("smartgrid/consumption", config.getTopic());
        assertEquals("smartgrid-client", config.getClientId());
    }
}
