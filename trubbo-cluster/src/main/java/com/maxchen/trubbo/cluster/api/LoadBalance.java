package com.maxchen.trubbo.cluster.api;

import java.util.List;

public interface LoadBalance {
    String select(List<String> list);
}
