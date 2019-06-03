package com.star.servlets;

import com.star.http.GPRequest;
import com.star.http.GPResponse;
import com.star.http.GPservlet;

import java.io.UnsupportedEncodingException;

/**
 * @Author: StarC
 * @Date: 2019/5/26 10:54
 * @Description:
 */
public class MyServlet extends GPservlet {
    @Override
    public void doGet(GPRequest request, GPResponse response) {
        try {
            response.write(request.getParameter("name")+" handsome");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(GPRequest request, GPResponse response) {

        doGet(request,response);
    }
}
