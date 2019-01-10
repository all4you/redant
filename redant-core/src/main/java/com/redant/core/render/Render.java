package com.redant.core.render;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 结果抽象接口，每个Controller的返回结果类型都是一个Render的实现类
 * @author gris.wang
 * @date 2017-10-20
 */
public interface Render {

	/**
	 * 获得往Channel中write的对象
	 * @return
	 * @throws Exception
	 */
	FullHttpResponse response();



}
