package com.star.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:52
 * @Description:
 */
public class GPRequest {

    ChannelHandlerContext ctx;
    HttpRequest r;
    public GPRequest(ChannelHandlerContext ctx, HttpRequest r) {

        this.ctx = ctx;
        this.r = r;
    }

    public String getUri(){
        return r.getUri();
    }
    public String getMethod(){
        return r.getMethod().name();
    }

    public Map<String, List<String>> getParameters(){

        QueryStringDecoder decoder = new QueryStringDecoder(r.getUri());
        return  decoder.parameters();
    }

    public String getParameter(String name){
        Map<String,List<String>> params = getParameters();
        List<String> param = params.get(name);
        if(null == param){
            return null;
        }else{
            return param.get(0);
        }
    }
}
