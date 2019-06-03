package com.star.netty.rpc.register;

import com.star.netty.rpc.msg.InvokeMsg;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: StarC
 * @Date: 2019/6/2 17:36
 * @Description:
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    //注册中心注册的服务需要有一个容器；
    public static ConcurrentHashMap<String,Object> registryMap = new ConcurrentHashMap<>();

    private List<String> classCache = new ArrayList<>();

    //约定，只要provider包下面的所有类都认为是一个可以对外服务的实现类 com.star.netty.rpc.provider


    public RegistryHandler() {
        scanClass("com.star.netty.rpc.provider");
        doRegister();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();

        //客户端传过来的调用信息
        InvokeMsg request = (InvokeMsg) msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz  = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
            result = method.invoke(clazz,request.getValues());

        }
        ChannelFuture channelFuture = ctx.writeAndFlush(result);

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    //IOC容器
    private void scanClass(String packageName){

        URL url = this.getClass().getClassLoader().getResource((packageName.replaceAll("\\.", "/")));
        File dir = new File(url.getFile());
       for(File file : dir.listFiles()){
           if(file.isDirectory()){
               scanClass(packageName+"."+file.getName());
           }else{
               System.out.println("fileName: "+file.getName());
               String className = packageName+"."+file.getName().replaceAll(".class","").trim();
               classCache.add(className);
           }

       }
    }

    //把扫描到class实例化，放到map中，这就是注册过程
    //注册的服务名字，接口名字，约定优于配置
    public void doRegister(){

        if(classCache.size() == 0)  return ;
        for(String className : classCache){
            try {
                Class<?> clazz = Class.forName(className);
                //服务名称
                Class<?> interfaces = clazz.getInterfaces()[0];
                String name = interfaces.getName();
                Object instance = clazz.newInstance();
                registryMap.put(interfaces.getName(),clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
