package com.star.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:52
 * @Description:
 */
public class GPResponse {

    ChannelHandlerContext ctx;
    HttpRequest r;
    public GPResponse(ChannelHandlerContext ctx, HttpRequest r) {

        this.ctx = ctx;
        this.r = r;
    }

    public void write(String out) throws UnsupportedEncodingException {

        if(out == null){
            return ;
        }
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            response.headers().set(CONTENT_TYPE,"text/json");
            response.headers().set(CONTENT_LENGTH,response.content().readableBytes());
            response.headers().set(EXPIRES,0);
            if(HttpHeaders.isKeepAlive(r)){
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
        } finally {
            ctx.flush();

        }
    }
}
