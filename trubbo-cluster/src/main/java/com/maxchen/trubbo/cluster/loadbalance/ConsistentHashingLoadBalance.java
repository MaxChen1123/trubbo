package com.maxchen.trubbo.cluster.loadbalance;

import com.maxchen.trubbo.cluster.ClusterProtocol;
import com.maxchen.trubbo.cluster.api.LoadBalance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashingLoadBalance implements LoadBalance {
    private static final TreeMap<Integer, String> nodes = new TreeMap<>();

    @Override
    public String select(List<String> list) {
        addNodes(list);
        Map.Entry<Integer, String> entry = nodes.ceilingEntry(
                getHashCode(ClusterProtocol.getClusterProtocolId().toString()));
        String value = entry.getValue();
        nodes.clear();
        return value;
    }

    public void addNodes(List<String> list) {
        for (String node : list) {
            nodes.put(getHashCode(node), node);
        }
    }


    public int getHashCode(String origin) {

        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < origin.length(); i++) {
            hash = (hash ^ origin.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);

        return hash;
    }
}
