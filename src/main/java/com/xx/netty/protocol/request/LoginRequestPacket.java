package com.xx.netty.protocol.request;

import com.xx.netty.protocol.Packet;
import lombok.Data;

import static com.xx.netty.protocol.command.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {
    private String userId;

    private String userName;

    private String password;

    @Override
    public Byte getCommand() {
        return LOGIN_REQUEST;
    }
}