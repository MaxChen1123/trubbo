package com.maxchen.trubbo.remoting.netty;

import com.maxchen.trubbo.remoting.codec.TrubboCodec;
import com.maxchen.trubbo.remoting.netty.api.ChannelHandler;
import com.maxchen.trubbo.remoting.netty.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NettyServer implements Server {
    private ServerBootstrap bootstrap;
    private int port;
    private ChannelHandler handler;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(int port, ChannelHandler handler) {
        this.port = port;
        this.handler = handler;
        bootstrap = new ServerBootstrap();
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        workerGroup = NettyEventLoopFactory.eventLoopGroup(
                // TODO
                4,
                "NettyServerWorker");
        NettyServerHandler nettyServerHandler = new NettyServerHandler(handler);

        // 初始化ServerBootstrap，指定boss和worker EventLoopGroup
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 注册Decoder和Encoder
                                .addLast("decoder", TrubboCodec.getDecoder())
                                .addLast("encoder", TrubboCodec.getEncoder())
                                // 注册IdleStateHandler
                                // TODO
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, 10000, MILLISECONDS))
                                // 注册NettyServerHandler
                                .addLast("handler", nettyServerHandler);
                    }
                });

    }

    @Override
    public void bind() {
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(port));
        channelFuture.syncUninterruptibly(); // 等待bind操作完成
    }

    @Override
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
