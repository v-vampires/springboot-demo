package com.xx.netty.server;

import com.xx.netty.codec.PacketDecoder;
import com.xx.netty.codec.PacketEncoder;
import com.xx.netty.protocol.request.LoginRequestPacket;
import com.xx.netty.protocol.request.MessageRequestPacket;
import com.xx.netty.protocol.response.LoginResponsePacket;
import com.xx.netty.protocol.Packet;
import com.xx.netty.protocol.PacketCodeC;
import com.xx.netty.protocol.response.MessageResponsePacket;
import com.xx.netty.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author yifanl
 * @Date 2020/6/8 9:40
 */
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        ch.pipeline().addLast(new PacketDecoder());

                        // 登录请求处理器
                        ch.pipeline().addLast(new LoginRequestHandler());
                        ch.pipeline().addLast(new AuthHandler());
                        // 单聊消息请求处理器
                        ch.pipeline().addLast(new MessageRequestHandler());
                        // 创建群请求处理器
                        ch.pipeline().addLast(new CreateGroupRequestHandler());
                        // 加群请求处理器
                        ch.pipeline().addLast(new JoinGroupRequestHandler());
                        // 退群请求处理器
                        ch.pipeline().addLast(new QuitGroupRequestHandler());
                        // 获取群成员请求处理器
                        ch.pipeline().addLast(new ListGroupMembersRequestHandler());
                        // 登出请求处理器
                        ch.pipeline().addLast(new LogoutRequestHandler());

                        ch.pipeline().addLast(new PacketEncoder());
                    }
                })
                .bind(9112);

    }

    private static class ServerHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(new Date() + ": 客户端开始登录……");
            ByteBuf requestByteBuf = (ByteBuf) msg;

            Packet packet = PacketCodeC.INSTANCE.decode(requestByteBuf);

            // 判断是否是登录请求数据包
            if (packet instanceof LoginRequestPacket) {
                // 登录流程
                LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;

                LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
                loginResponsePacket.setVersion(packet.getVersion());
                if (valid(loginRequestPacket)) {
                    loginResponsePacket.setSuccess(true);
                    System.out.println(new Date() + ": 登录成功!");
                } else {
                    loginResponsePacket.setReason("账号密码校验失败");
                    loginResponsePacket.setSuccess(false);
                    System.out.println(new Date() + ": 登录失败!");
                }
                // 登录响应
                ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginResponsePacket);
                ctx.channel().writeAndFlush(responseByteBuf);
            }else if (packet instanceof MessageRequestPacket) {
                // 处理消息
                MessageRequestPacket messageRequestPacket = ((MessageRequestPacket) packet);
                System.out.println(new Date() + ": 收到客户端消息: " + messageRequestPacket.getMessage());

                MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
                messageResponsePacket.setMessage("服务端回复【" + messageRequestPacket.getMessage() + "】");
                ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), messageResponsePacket);
                ctx.channel().writeAndFlush(responseByteBuf);
            }
        }
        private boolean valid(LoginRequestPacket loginRequestPacket) {
            return true;
        }
    }

    private static class FirstServerHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            ByteBuf out = getByteBuf(ctx, "客户端启动发送消息");
            ctx.channel().writeAndFlush(out);
        }

        /**
         * channelRead 这个方法在接收到客户端发来的数据之后被回调
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
            System.out.println("====服务端写出数据===");
            ByteBuf out = getByteBuf(ctx, "你好，欢迎关注我的微信公众号，《闪电侠的博客》!");
            ctx.channel().writeAndFlush(out);
        }

        private ByteBuf getByteBuf(ChannelHandlerContext ctx, String content) {
            byte[] bytes = content.getBytes(Charset.forName("utf-8"));
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(bytes);
            return buffer;
        }
    }
}
