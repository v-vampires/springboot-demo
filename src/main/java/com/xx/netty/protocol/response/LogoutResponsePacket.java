package com.xx.netty.protocol.response;

import com.xx.netty.protocol.Packet;
import lombok.Data;

import static com.xx.netty.protocol.command.Command.LOGOUT_RESPONSE;

@Data
public class LogoutResponsePacket extends Packet {

    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {

        return LOGOUT_RESPONSE;
    }
}
