package com.star.netty.rpc.consumer.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author: StarC
 * @Date: 2019/6/2 18:32
 * @Description:
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private Object result;
    public Object getResult(){
        return this.result;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       this.result = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
    }
}
