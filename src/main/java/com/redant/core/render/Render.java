package com.redant.core.render;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 结果抽象接口，每个Controller的返回结果类型都是一个Render的实现类
 * @author gris.wang
 * @create 2017-10-20
 */
public interface Render {

	/**
	 * 获得Netty的返回对象
	 * @return
	 * @throws Exception
	 */
	FullHttpResponse render() throws Exception;

}
