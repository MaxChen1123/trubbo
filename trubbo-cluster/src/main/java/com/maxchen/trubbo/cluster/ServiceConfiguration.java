package com.maxchen.trubbo.cluster;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.common.configuration.Configuration;
import com.maxchen.trubbo.registry.TrubboRegistry;
import lombok.Getter;

import java.net.URISyntaxException;

// TODO to be replaced
public class ServiceConfiguration implements Configuration {
    @Getter
    private final String serviceName;

    public ServiceConfiguration(String serviceName) {
        this.serviceName = serviceName;
    }


    @Override
    public String getProperty(String key, String defaultValue) {
        String config = TrubboRegistry.CONFIGURATION_MAP.getOrDefault(serviceName, null);
        if (config == null) {
            return defaultValue;
        }
        try {
            URL url = new URL(config);
            return url.getParameter(key, defaultValue);
        } catch (URISyntaxException e) {
            return defaultValue;
        }
    }
}
