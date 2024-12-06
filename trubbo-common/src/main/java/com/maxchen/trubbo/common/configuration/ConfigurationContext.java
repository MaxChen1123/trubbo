package com.maxchen.trubbo.common.configuration;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.URL.URL;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationContext {
    public static final Map<String, String> SYSTEM_CONFIGURATION_MAP = System.getenv();
    public static final Map<String, String> REGISTRY_CONFIGURATION_MAP = new ConcurrentHashMap<>();
    public static final Map<Object, Object> SPRING_CONFIGURATION_MAP = new ConcurrentHashMap<Object, Object>();

    public static String getServiceConfigProperty(String serviceName, String key, String defaultValue) {
        String config = REGISTRY_CONFIGURATION_MAP.getOrDefault(serviceName, null);
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

    public static String getMethodConfigProperty(String serviceName, String methodName, String key, String defaultValue) {
        String config = REGISTRY_CONFIGURATION_MAP.getOrDefault(serviceName + ":" + methodName, null);
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

    public static String getSpringServiceConfigProperty(String serviceName, String key, String defaultValue) {
        return (String) SPRING_CONFIGURATION_MAP.getOrDefault("trubbo." + serviceName + '.' + key, defaultValue);
    }

    public static String getSpringMethodConfigProperty(String serviceName, String methodName, String key, String defaultValue) {
        return (String) SPRING_CONFIGURATION_MAP.getOrDefault("trubbo." + serviceName + '.' + methodName + '.' + key, defaultValue);
    }

    public static String getProperty(String key, String defaultValue) {
        RpcContext context = RpcContext.getContext();
        String springMethodConfigProperty = getSpringMethodConfigProperty(context.getServiceName(), context.getMethodName(), key, null);
        if (springMethodConfigProperty != null) {
            return springMethodConfigProperty;
        }
        String springServiceConfigProperty = getSpringServiceConfigProperty(context.getServiceName(), key, null);
        if (springServiceConfigProperty != null) {
            return springServiceConfigProperty;
        }
        String methodConfigProperty = getMethodConfigProperty(context.getServiceName(), context.getMethodName(), key, null);
        if (methodConfigProperty != null) {
            return methodConfigProperty;
        } else return getServiceConfigProperty(context.getServiceName(), key, defaultValue);
    }
}
