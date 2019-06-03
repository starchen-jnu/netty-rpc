package com.star.netty.rpc.consumer.proxy;

import com.star.netty.rpc.msg.InvokeMsg;
import com.star.netty.rpc.register.RegistryHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author: StarC
 * @Date: 2019/6/2 18:11
 * @Description:
 */
public class RpcProxy {

    public static <T> T create(Class<?>clazz){

        MethodProxy methodProxy = new MethodProxy(clazz);
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, methodProxy);
        return  result ;
    }
}
class MethodProxy implements InvocationHandler{


    private Class<?> clazz;
    public MethodProxy(Class<?> clazz) {
        this.clazz = clazz;
    }

    //代理，调用Rpc接口的每一个方法时候，实际上是发起一次网路请求
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //如果传进来是一个已经实现的具体类（直接忽略）
        if(Object.class.equals(method.getDeclaringClass())){

            return method.invoke(this,args);
        }else{
            //如果传进来的是一个接口，调用远程调用
            return rpcInvoke(method,args);
        }

    }
    public Object rpcInvoke(Method method, Object[] args){

        InvokeMsg msg = new InvokeMsg();
        msg.setClassName(this.clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParames(method.getParameterTypes());
        msg.setValues(args);

        EventLoopGroup group = new NioEventLoopGroup();
      final   RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //处理拆包、粘包的解、编码器
                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            pipeline.addLast("frameEncder",new LengthFieldPrepender(4));

                            //处理序列化的解
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));



                            //业务逻辑处理
                            pipeline.addLast(rpcProxyHandler);

                        }
                    });
            ChannelFuture f = b.connect("localhost", 8080).sync();
             f.channel().writeAndFlush(msg).sync();

            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
        return rpcProxyHandler.getResult();
    }
}
