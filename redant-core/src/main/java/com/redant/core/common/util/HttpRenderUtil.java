package com.redant.core.common.util;

import com.alibaba.fastjson.JSONObject;
import com.redant.core.common.html.DefaultHtmlMaker;
import com.redant.core.common.html.HtmlMakerEnum;
import com.redant.core.common.html.HtmlMakerFactory;
import com.redant.core.common.view.Page404;
import com.redant.core.common.view.Page500;
import com.redant.core.common.view.PageError;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpRenderUtil
 * @author gris.wang
 * @date 2017-10-20
 */
public class HttpRenderUtil {

	public static final String EMPTY_CONTENT = "";

	public static final String NO_RESPONSE = "No Response";

	public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

	public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

	public static final String CONTENT_TYPE_XML = "text/xml;charset=UTF-8";

	public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";


	private HttpRenderUtil(){

	}

	/**
	 * 输出纯Json字符串
	 */
	public static FullHttpResponse renderJSON(Object json){
		return render(json, CONTENT_TYPE_JSON);
	}
	
	/**
	 * 输出纯字符串
	 */
	public static FullHttpResponse renderText(Object text) {
		return render(text, CONTENT_TYPE_TEXT);
	}
	
	/**
	 * 输出纯XML
	 */
	public static FullHttpResponse renderXML(Object xml) {
		return render(xml, CONTENT_TYPE_XML);
	}
	
	/**
	 * 输出纯HTML
	 */
	public static FullHttpResponse renderHTML(Object html) {
		return render(html, CONTENT_TYPE_HTML);
	}

	/**
	 * response输出
	 * @param content 内容
	 * @param contentType 返回类型
	 * @return 响应对象
	 */
	public static FullHttpResponse render(Object content, String contentType){
		byte[] bytes = HttpRenderUtil.getBytes(content);
		ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
		if(contentType!=null && contentType.trim().length()>0) {
			response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
		response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

	/**
	 * 404NotFoundResponse
	 * @return 响应对象
	 */
	public static FullHttpResponse getNotFoundResponse(){
		String content = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),Page404.HTML,null);
		return render(content, CONTENT_TYPE_HTML);
	}

	/**
	 * ServerErrorResponse
	 * @return 响应对象
	 */
	public static FullHttpResponse getServerErrorResponse(){
		JSONObject object = new JSONObject();
		object.put("code",500);
		object.put("message","Server Internal Error!");
		return render(object, CONTENT_TYPE_JSON);
	}

	/**
	 * ErrorResponse
	 * @param errorMessage 错误信息
	 * @return 响应对象
	 */
	public static FullHttpResponse getErrorResponse(String errorMessage){
		JSONObject object = new JSONObject();
		object.put("code",300);
		object.put("message",errorMessage);
		return render(object, CONTENT_TYPE_JSON);
	}

	/**
	 * 转换byte
	 * @param content 内容
	 * @return 响应对象
	 */
	private static byte[] getBytes(Object content){
		if(content==null){
			return EMPTY_CONTENT.getBytes(CharsetUtil.UTF_8);
		}
		String data = content.toString();
		data = (data==null || data.trim().length()==0)?EMPTY_CONTENT:data;
		return data.getBytes(CharsetUtil.UTF_8);
	}

}
