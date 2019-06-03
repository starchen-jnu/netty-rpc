package com.star.server;

import com.star.http.GPRequest;
import com.star.http.GPResponse;
import com.star.servlets.MyServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:36
 * @Description:
 */
public class GPTomcatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){

            HttpRequest r = (HttpRequest) msg;

            GPRequest request = new GPRequest(ctx,r);
            GPResponse response = new GPResponse(ctx,r);

            new MyServlet().doGet(request,response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
