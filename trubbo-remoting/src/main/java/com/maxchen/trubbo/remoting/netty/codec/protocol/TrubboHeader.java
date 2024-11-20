package com.maxchen.trubbo.remoting.netty.codec.protocol;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.common.util.TrubboProtocolUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrubboHeader {
    public static final short MAGIC = (short) 0xAAAA;
    private byte info; // 第一个bit表示是否是请求 第二个表示是否是心跳,剩下的六个表示状态码
    private long messageId; // 消息ID
    private int size; // 消息体长度


    public static void setContext(RpcContext context, TrubboHeader header) {
        byte info = header.getInfo();
        context.setRequestId(header.getMessageId());
        context.setRequest(TrubboProtocolUtil.isRequest(info));
        context.setHeartBeat(TrubboProtocolUtil.isHeartBeat(info));
        context.setOneWay(TrubboProtocolUtil.isOneWay(info));
    }
}


