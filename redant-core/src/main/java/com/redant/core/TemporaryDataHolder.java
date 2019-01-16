package com.redant.core;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.*;

/**
 * TemporaryDataHolder 将请求消息对象存放在静态ThreadLocal的实例变量中
 * @author houyi.wh
 * @date 2017-10-20
 */
public class TemporaryDataHolder {

	/**
	 * 使用FastThreadLocal替代JDK自带的ThreadLocal以提升并发性能
	 */
	private static final FastThreadLocal<Map<String,Object>> FAST_THREAD_LOCAL = new FastThreadLocal<>();


	private enum HolderType {
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
		/**
		 * cookie
		 */
		COOKIE("cookie"),
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

    private static Map<String,Object> getLocalMap(){
        Map<String,Object> localMap = FAST_THREAD_LOCAL.get();
        if(localMap==null){
            localMap = new HashMap<>();
        }
        return localMap;
    }

	private static void store(HolderType holderType,Object value){
        Map<String,Object> localMap = getLocalMap();
		localMap.put(holderType.getType(),value);
		FAST_THREAD_LOCAL.set(localMap);
	}

	private static Object get(HolderType holderType){
        Map<String,Object> localMap = getLocalMap();
		return localMap.get(holderType.getType());
	}

    private static void remove(HolderType holderType){
        Map<String,Object> localMap = getLocalMap();
        localMap.remove(holderType.getType());
        FAST_THREAD_LOCAL.set(localMap);
    }

    public static void removeAll(){
        FAST_THREAD_LOCAL.remove();
    }



    public static void storeHttpRequest(HttpRequest httpRequest){
		if(httpRequest!=null){
			TemporaryDataHolder.store(HolderType.REQUEST,httpRequest);
		}
	}

	public static void storeHttpResponse(HttpResponse httpResponse){
		if(httpResponse!=null){
			TemporaryDataHolder.store(HolderType.RESPONSE,httpResponse);
		}
	}

	public static void storeForceClose(Boolean forceClose){
		if(forceClose!=null){
			TemporaryDataHolder.store(HolderType.FORCE_CLOSE,forceClose);
		}
	}

	public static void storeContext(ChannelHandlerContext context){
		if(context!=null){
			TemporaryDataHolder.store(HolderType.CONTEXT,context);
		}
	}

	public static void storeCookie(Cookie cookie){
		if(cookie!=null){
			Set<Cookie> cookies = TemporaryDataHolder.loadCookies();
			if(CollectionUtil.isEmpty(cookies)){
				cookies = new HashSet<>();
			}
			cookies.add(cookie);
			TemporaryDataHolder.store(HolderType.COOKIE,cookies);
		}
	}

	public static HttpRequest loadHttpRequest(){
	    Object object = TemporaryDataHolder.get(HolderType.REQUEST);
		return object==null?null:(HttpRequest)object;
	}

	public static FullHttpResponse loadHttpResponse(){
	    Object object = TemporaryDataHolder.get(HolderType.RESPONSE);
		return object==null?null:(FullHttpResponse)object;
	}

	public static boolean loadForceClose(){
	    Object object = TemporaryDataHolder.get(HolderType.FORCE_CLOSE);
		return object != null && (boolean) object;
	}

	public static ChannelHandlerContext loadContext(){
	    Object object = TemporaryDataHolder.get(HolderType.CONTEXT);
		return object==null?null:(ChannelHandlerContext)object;
	}

	public static Set<Cookie> loadCookies(){
	    Object object = TemporaryDataHolder.get(HolderType.COOKIE);
		return object==null?null:(Set<Cookie>)object;
	}

}
