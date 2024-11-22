package com.maxchen.trubbo.cluster.loadbalance;

import com.maxchen.trubbo.cluster.api.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get((int) (Math.random() * list.size()));
    }
}
