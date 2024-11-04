package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.remoting.api.ChannelHandler;
import com.maxchen.trubbo.remoting.api.Client;
import com.maxchen.trubbo.remoting.codec.TrubboCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

import static com.maxchen.trubbo.remoting.netty.NettyEventLoopFactory.eventLoopGroup;
import static com.maxchen.trubbo.remoting.netty.NettyEventLoopFactory.socketChannelClass;


public class NettyClient implements Client {
    private String host;
    private int port;
    private ChannelHandler handler;
    private Bootstrap bootstrap;
    private Channel channel;
    // TODO thread 数量
    private static final EventLoopGroup NIO_EVENT_LOOP_GROUP = eventLoopGroup(4, "NettyClientWorker");


    public NettyClient(String host, int port, ChannelHandler handler) {
        this.host = host;
        this.port = port;
        this.handler = handler;
        NettyClientHandler nettyClientHandler = new NettyClientHandler(handler);
        bootstrap = new Bootstrap();
        bootstrap.group(NIO_EVENT_LOOP_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout())
                .channel(socketChannelClass());

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                        // 注册ChannelHandler
                        .addLast("decoder", TrubboCodec.getDecoder())
                        .addLast("encoder", TrubboCodec.getEncoder())
                        .addLast("handler", nettyClientHandler);
            }
        });
    }

    @Override
    public void connect() {
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress(host, port));
        connect.syncUninterruptibly();
        channel = connect.channel();
    }

    @Override
    public void disconnect() {
        ChannelFuture disconnect = channel.disconnect();
        disconnect.syncUninterruptibly();
    }

    @Override
    public void send(Object message) {
        channel.writeAndFlush(message);
    }
}
