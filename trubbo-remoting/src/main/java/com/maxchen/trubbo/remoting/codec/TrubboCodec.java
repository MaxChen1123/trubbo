package com.maxchen.trubbo.remoting.codec;

public class TrubboCodec {
    public static TrubboDecoder getDecoder()
    {
        return new TrubboDecoder();
    }
    public static TrubboEncoder getEncoder()
    {
        return new TrubboEncoder();
    }
}
