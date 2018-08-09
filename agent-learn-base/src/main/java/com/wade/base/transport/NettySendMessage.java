package com.wade.base.transport;

import com.wade.base.transport.bean.RpcRequest;
import com.wade.base.transport.bean.RpcResponse;
import com.wade.base.transport.channel.NettyMessageChannel;
import com.wade.base.transport.code.RpcDecoder;
import com.wade.base.transport.code.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author :lwy
 * @date 2018/8/9 14:31
 * netty客户端
 */
public class NettySendMessage implements SendMessage {

    private final String host;
    private final int port;

    public NettySendMessage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse send(RpcRequest request) {


        final NettyMessageChannel nettyMessageChannel = new NettyMessageChannel();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class))
                            .addLast(new RpcDecoder(RpcResponse.class))
                            .addLast(nettyMessageChannel);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器---修改为连接
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();

            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            //返回RPC响应对象
            return nettyMessageChannel.getResponse();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
