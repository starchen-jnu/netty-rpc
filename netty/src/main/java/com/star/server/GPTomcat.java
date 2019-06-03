package com.star.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:17
 * @Description:
 */
public class GPTomcat {

    public void start(int port) throws InterruptedException {


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

                    //无锁化串口编程

                    //业务逻辑链路
                    client.pipeline().addLast(new HttpResponseEncoder());

                    //解码器
                    client.pipeline().addLast(new HttpRequestDecoder());

                    //业务逻辑处理
                    client.pipeline().addLast(new GPTomcatHandler());
                }
                //配置信息
            }).option(ChannelOption.SO_BACKLOG,128)//针对主线程
                    .childOption(ChannelOption.SO_KEEPALIVE,true);//子线程配置

            ChannelFuture channelFuture = server.bind(port).sync();

            System.out.println("STARTomcat"+port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }
    }

    public static void main(String[] args)  {
        try {
            new GPTomcat().start(8080);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
