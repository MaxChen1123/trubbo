package com.maxchen.trubbo.remoting.netty.codec.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrubboMessage {
    private TrubboHeader header;
    private Object body;
}
