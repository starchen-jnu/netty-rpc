package com.star.http;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:47
 * @Description:
 */
public abstract class GPservlet {

    public abstract void doGet(GPRequest request,GPResponse response);
    public abstract void doPost(GPRequest request,GPResponse response);
}
