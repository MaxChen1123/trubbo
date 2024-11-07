package com.maxchen.trubbo.common.URL;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class URL implements Serializable {
    private static final long serialVersionUID = 1L;
    private String protocol;
    private String host;
    private int port;
    private String path;
    private Map<String, String> parameters;

    public URL(String urlString) throws URISyntaxException {
        parameters = new HashMap<>();
        parseUrl(urlString);
    }

    private void parseUrl(String urlString) throws URISyntaxException {
        URI uri = new URI(urlString);
        this.protocol = uri.getScheme();
        this.host = uri.getHost();
        this.port = uri.getPort() == -1 ? getDefaultPort() : uri.getPort();
        this.path = uri.getPath();

        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : null;
                parameters.put(key, value);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://").append(host);

        if (port != -1) {
            sb.append(":").append(port);
        }

        sb.append(path);

        if (!parameters.isEmpty()) {
            sb.append("?");
            parameters.forEach((key, value) -> {
                sb.append(key).append("=").append(value).append("&");
            });
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public String getParameter(String key, String defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    private int getDefaultPort() {
        return switch (protocol) {
            case "http" -> 80;
            case "https" -> 443;
            case "ftp" -> 21;
            default -> -1; // 未知协议的默认端口
        };
    }
}
