package com.maxchen.trubbo.common.util;

public class TrubboProtocolUtil {
    boolean isHeartBeat(byte info){
        return (info&(0x40))!=0;
    }
    boolean isRequest(byte info){
        return (info&(0x80))!=0;
    }
    byte setHeartBeat(byte info){
        return (byte) (info|(0x40));
    }
    byte setRequest(byte info){
        return (byte) (info|(0x80));
    }
    byte clearHeartBeat(byte info){
        return (byte) (info&(0xBF));
    }
    byte clearRequest(byte info){
        return (byte) (info&(0x7F));
    }
}
