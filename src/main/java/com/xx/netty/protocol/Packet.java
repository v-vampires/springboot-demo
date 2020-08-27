package com.xx.netty.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author yifanl
 * @Date 2020/6/8 22:36
 */
@Data
public abstract class Packet {
    /**
     * 协议版本
     */
    @JSONField(deserialize = false, serialize = false)
    private Byte version = 1;
    /**
     * 指令
     */
    @JSONField(serialize = false)
    public abstract Byte getCommand();
}
