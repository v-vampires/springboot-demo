package com.xx.netty.protocol.request;

import com.xx.netty.protocol.Packet;
import lombok.Data;

import static com.xx.netty.protocol.command.Command.LOGOUT_REQUEST;

@Data
public class LogoutRequestPacket extends Packet {
    @Override
    public Byte getCommand() {

        return LOGOUT_REQUEST;
    }
}
