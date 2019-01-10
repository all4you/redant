package com.redant.core.common.enums;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author gris.wang
 * @date 2017/12/1
 **/
public enum RequestMethod {
    /**
     * GET
     */
    GET(HttpMethod.GET),
    /**
     * HEAD
     */
    HEAD(HttpMethod.HEAD),
    /**
     * POST
     */
    POST(HttpMethod.POST),
    /**
     * PUT
     */
    PUT(HttpMethod.PUT),
    /**
     * PATCH
     */
    PATCH(HttpMethod.PATCH),
    /**
     * DELETE
     */
    DELETE(HttpMethod.DELETE),
    /**
     * OPTIONS
     */
    OPTIONS(HttpMethod.OPTIONS),
    /**
     * TRACE
     */
    TRACE(HttpMethod.TRACE);

    HttpMethod httpMethod;

    RequestMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpMethod getHttpMethod(RequestMethod requestMethod){
        for(RequestMethod method : values()){
            if(requestMethod==method){
                return method.httpMethod;
            }
        }
        return null;
    }

}