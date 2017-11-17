package com.redant.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * DataHolder 将请求消息对象存放在静态ThreadLocal的实例变量中
 * @author gris.wang
 * @since 2017-10-20
 */
public class DataHolder {

	/**
	 * 使用FastThreadLocal替代JDK自带的ThreadLocal以提升并发性能
	 * private static final ThreadLocal<Map<String,Object>> LOCAL_DATA = new ThreadLocal<Map<String,Object>>();
	 */
	private static final FastThreadLocal<Map<String,Object>> LOCAL_DATA = new FastThreadLocal<Map<String,Object>>();


	public enum HolderType{
		/**
		 * request
		 */
		REQUEST("request"),
		/**
		 * response
		 */
		RESPONSE("response"),
		/**
		 * context
		 */
		CONTEXT("context"),
		/**
		 * forceClose
		 */
		FORCE_CLOSE("forceClose"),
		;

		private String type;

		HolderType(String type){
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	//===================================


	public static void store(HolderType holderType,Object value){
		Map<String,Object> map = LOCAL_DATA.get();
		if(map==null){
			map = new HashMap<String,Object>();
		}
		map.put(holderType.getType(),value);
		LOCAL_DATA.set(map);
	}

	public static Object get(HolderType holderType){
		return LOCAL_DATA.get()!=null?LOCAL_DATA.get().get(holderType.getType()):null;
	}

	public static HttpRequest getHttpRequest(){
		return (HttpRequest)DataHolder.get(DataHolder.HolderType.REQUEST);
	}

	public static FullHttpResponse getHttpResponse(){
		return (FullHttpResponse)DataHolder.get(DataHolder.HolderType.RESPONSE);
	}

	public static boolean getForceClose(){
		return (boolean)DataHolder.get(DataHolder.HolderType.FORCE_CLOSE);
	}

	public static ChannelHandlerContext getContext(){
		return (ChannelHandlerContext)DataHolder.get(DataHolder.HolderType.CONTEXT);
	}

	public static void remove(HolderType holderType){
		Map<String,Object> map = LOCAL_DATA.get();
		if(map==null){
			map = new HashMap<String,Object>();
		}
		map.remove(holderType.getType());
		LOCAL_DATA.set(map);
	}

	public static void removeAll(){
		LOCAL_DATA.remove();
	}

}
