package com.star.netty.rpc.consumer;

import com.star.netty.rpc.api.RpcHelloI;
import com.star.netty.rpc.consumer.proxy.RpcProxy;
import com.star.netty.rpc.provider.RpcHelloImpl;

import java.lang.reflect.Proxy;

/**
 * @Author: StarC
 * @Date: 2019/6/2 17:15
 * @Description:
 */
public class RpcConsumer {

    public static void main(String[] args) {

        RpcHelloI rpcHelloI = RpcProxy.create(RpcHelloI.class);
        System.out.println(rpcHelloI.hello("star"));


    }
}
