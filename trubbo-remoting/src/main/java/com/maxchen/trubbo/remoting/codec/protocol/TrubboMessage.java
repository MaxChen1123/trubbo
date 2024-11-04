package com.maxchen.trubbo.remoting.codec.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrubboMessage {
    private TrubboHeader header;
    private Object body;
}
