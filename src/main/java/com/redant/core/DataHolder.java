package com.redant.core;

import io.netty.handler.codec.http.HttpRequest;

/**
 * DataHolder 将请求消息对象存放在静态ThreadLocal的实例变量中，保证每个请求的request能通过静态方法获取，从而减少传递参数。
 */
public class DataHolder {
	
	private static final ThreadLocal<Object> LOCAL_DATA = new ThreadLocal<Object>();
	
	public static void storeRequest(HttpRequest request){
		LOCAL_DATA.set(request);
	}
	
	public static Object getRequest(){
		return LOCAL_DATA.get();
	}

	public static void removeRequest(){
		LOCAL_DATA.remove();
	}
}
