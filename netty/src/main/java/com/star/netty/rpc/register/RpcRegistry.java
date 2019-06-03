package com.star.netty.rpc.register;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Author: StarC
 * @Date: 2019/6/2 17:15
 * @Description:
 */
public class RpcRegistry {



    private int port;
    public RpcRegistry(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {

        //主从线程模型
        //boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //netty服务
        ServerBootstrap server = new ServerBootstrap();
        try {

            //链路性编程
            server.group(bossGroup,workerGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    //子线程处理，handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {


                            ChannelPipeline pipeline = client.pipeline();
                            //处理拆包、粘包的解、编码器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            pipeline.addLast(new LengthFieldPrepender(4));

                            //处理序列化的解
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));



                            //业务逻辑处理
                            pipeline.addLast(new RegistryHandler());
                        }
                        //配置信息
                    }).option(ChannelOption.SO_BACKLOG,128)//针对主线程
                    .childOption(ChannelOption.SO_KEEPALIVE,true);//子线程配置

            ChannelFuture channelFuture = server.bind(port).sync();

            System.out.println("STARRPC"+port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }
    }

    public static void main(String[] args) throws InterruptedException {

        new RpcRegistry(8080).run();
    }
}
