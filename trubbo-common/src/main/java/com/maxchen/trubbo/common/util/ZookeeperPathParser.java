package com.maxchen.trubbo.common.util;

public class ZookeeperPathParser {
    public static String getServiceName(String path) {
        if (path == null || !path.startsWith("/trubbo/service/")) {
            throw new IllegalArgumentException("Invalid path format");
        }

        String[] segments = path.split("/");

        if (segments.length < 5) {
            throw new IllegalArgumentException("Path must contain at least serviceName and address");
        }
        return segments[3];
    }

    public static String getAddress(String path) {
        if (path == null || !path.startsWith("/trubbo/service/")) {
            throw new IllegalArgumentException("Invalid path format");
        }

        String[] segments = path.split("/");

        if (segments.length < 6) {
            throw new IllegalArgumentException("Path must contain at least serviceName and address");
        }
        return segments[5];
    }

    public static String getMethodKey(String path) {
        if (path == null || !path.startsWith("/trubbo/service/")) {
            throw new IllegalArgumentException("Invalid path format");
        }
        String[] segments = path.split("/");
        if (segments.length < 6) {
            throw new IllegalArgumentException("Path must contain at least serviceName and address");
        }
        return segments[3] + ":" + segments[5];
    }
}
