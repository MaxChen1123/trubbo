package com.maxchen.trubbo.common.configuration;

public interface Configuration {

    String getProperty(String key, String defaultValue);

    String getServiceName();
}
