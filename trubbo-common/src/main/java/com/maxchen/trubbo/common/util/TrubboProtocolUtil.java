package com.maxchen.trubbo.common.util;

public class TrubboProtocolUtil {
    public static boolean isHeartBeat(byte info) {
        return (info & (0x40)) != 0;
    }

    public static boolean isRequest(byte info) {
        return (info & (0x80)) != 0;
    }

    public static boolean isOneWay(byte info) {
        return (info & (0x20)) != 0;
    }

    public static byte setHeartBeat(byte info) {
        return (byte) (info | (0x40));
    }

    public static byte setRequest(byte info) {
        return (byte) (info | (0x80));
    }

    public static byte setOneWay(byte info) {
        return (byte) (info | (0x20));
    }

    public static byte clearHeartBeat(byte info) {
        return (byte) (info & (0xBF));
    }

    public static byte clearRequest(byte info) {
        return (byte) (info & (0x7F));
    }

    public static byte clearOneWay(byte info) {
        return (byte) (info & (0xDF));
    }
}
