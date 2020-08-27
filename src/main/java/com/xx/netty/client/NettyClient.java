package com.xx.netty.client;

import com.xx.netty.client.console.ConsoleCommandManager;
import com.xx.netty.client.console.LoginConsoleCommand;
import com.xx.netty.client.handler.*;
import com.xx.netty.codec.PacketDecoder;
import com.xx.netty.codec.PacketEncoder;
import com.xx.netty.protocol.Packet;
import com.xx.netty.protocol.request.LoginRequestPacket;
import com.xx.netty.protocol.PacketCodeC;
import com.xx.netty.protocol.request.MessageRequestPacket;
import com.xx.netty.protocol.response.LoginResponsePacket;
import com.xx.netty.protocol.response.MessageResponsePacket;
import com.xx.netty.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yifanl
 * @Date 2020/6/8 10:27
 */
public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9112;

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 7, 4));
                        ch.pipeline().addLast(new PacketDecoder());

                        // 登录响应处理器
                        ch.pipeline().addLast(new LoginResponseHandler());
                        // 收消息处理器
                        ch.pipeline().addLast(new MessageResponseHandler());
                        // 创建群响应处理器
                        ch.pipeline().addLast(new CreateGroupResponseHandler());
                        // 加群响应处理器
                        ch.pipeline().addLast(new JoinGroupResponseHandler());
                        // 退群响应处理器
                        ch.pipeline().addLast(new QuitGroupResponseHandler());
                        // 获取群成员响应处理器
                        ch.pipeline().addLast(new ListGroupMembersResponseHandler());
                        // 登出响应处理器
                        ch.pipeline().addLast(new LogoutResponseHandler());

                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if(future.isSuccess()){
                System.out.println(new Date() + ": 连接成功，启动控制台线程……");
                Channel channel = ((ChannelFuture) future).channel();
                startConsoleThread(channel);
            }else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            }else{
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager();
        new Thread(()->{
            while(!Thread.interrupted()){
                if(!SessionUtil.hasLogin(channel)){
                    loginConsoleCommand.exec(sc, channel);
                }else{
                    consoleCommandManager.exec(sc, channel);
                }
            }
        }).start();
    }

    private static class ClientHandler extends ChannelInboundHandlerAdapter{

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            System.out.println(new Date() + ": 客户端开始登录");

            // 创建登录对象
            LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
            loginRequestPacket.setUserId(UUID.randomUUID().toString());
            loginRequestPacket.setUserName("flash");
            loginRequestPacket.setPassword("pwd");

            // 编码
            ByteBuf buffer = PacketCodeC.INSTANCE.encode(ctx.alloc(), loginRequestPacket);

            // 写数据
            ctx.channel().writeAndFlush(buffer);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;

            Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);

            if (packet instanceof LoginResponsePacket) {
                LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;

                if (loginResponsePacket.isSuccess()) {
                    System.out.println(new Date() + ": 客户端登录成功");
                    //SessionUtil.markAsLogin(ctx.channel());
                } else {
                    System.out.println(new Date() + ": 客户端登录失败，原因：" + loginResponsePacket.getReason());
                }
            }else if (packet instanceof MessageResponsePacket) {
                MessageResponsePacket messageResponsePacket = (MessageResponsePacket) packet;
                System.out.println(new Date() + ": 收到服务端的消息: " + messageResponsePacket.getMessage());
            }

        }
    }

    private static class FirstClientHandler extends ChannelInboundHandlerAdapter {
        /**
         * 这个方法会在客户端连接建立成功之后被调用
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(new Date() + ": 客户端写出数据");
            // 1. 获取数据
            ByteBuf buffer = getByteBuf(ctx);
            // 2. 写数据
            ctx.channel().writeAndFlush(buffer);
        }

        private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
            // 1. 获取二进制抽象 ByteBuf
            ByteBuf buffer = ctx.alloc().buffer();

            // 2. 准备数据，指定字符串的字符集为 utf-8
            byte[] bytes = "你好，闪电侠!".getBytes(Charset.forName("utf-8"));

            // 3. 填充数据到 ByteBuf
            buffer.writeBytes(bytes);

            return buffer;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;

            System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
        }
    }
}
