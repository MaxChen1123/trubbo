package com.maxchen.trubbo.cluster.api;

public interface Configuration {

    String getProperty(String key, String defaultValue);

    String getServiceName();
}
