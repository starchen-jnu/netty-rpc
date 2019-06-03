package com.star.netty.rpc.provider;

import com.star.netty.rpc.api.RpcHelloI;

/**
 * @Author: StarC
 * @Date: 2019/6/2 17:17
 * @Description:
 */
public class RpcHelloImpl implements RpcHelloI {
    @Override
    public String hello(String name) {
        return "Hello:" + name+ "!";
    }
}
