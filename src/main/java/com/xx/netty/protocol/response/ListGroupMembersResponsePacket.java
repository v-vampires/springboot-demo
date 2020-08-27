package com.xx.netty.protocol.response;

import com.xx.netty.protocol.Packet;
import com.xx.netty.session.Session;
import lombok.Data;

import java.util.List;

import static com.xx.netty.protocol.command.Command.LIST_GROUP_MEMBERS_RESPONSE;

@Data
public class ListGroupMembersResponsePacket extends Packet {

    private String groupId;

    private List<Session> sessionList;

    @Override
    public Byte getCommand() {

        return LIST_GROUP_MEMBERS_RESPONSE;
    }
}
