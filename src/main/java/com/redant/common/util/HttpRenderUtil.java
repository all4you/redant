package com.redant.common.util;

import com.redant.common.constants.CommonConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gris.wang
 * @create 2017-10-20
 */
public class HttpRenderUtil {

	private final static Logger logger = LoggerFactory.getLogger(HttpRenderUtil.class);

	public static final String EMPTY_CONTENT = "";

	public static final String NO_RESPONSE = "No Response";

	public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

	public static final String CONTENT_TYPE_TEXT = "text/plain;charset=UTF-8";

	public static final String CONTENT_TYPE_XML = "text/xml;charset=UTF-8";

	public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";

	
	/**
	 * 输出纯Json字符串
	 */
	public static FullHttpResponse renderJSON(byte[] json){
		return render(json, CONTENT_TYPE_JSON);
	}
	
	/**
	 * 输出纯字符串
	 */
	public static FullHttpResponse renderText(byte[] text) {
		return render(text, CONTENT_TYPE_TEXT);
	}
	
	/**
	 * 输出纯XML
	 */
	public static FullHttpResponse renderXML(byte[] xml) {
		return render(xml, CONTENT_TYPE_XML);
	}
	
	/**
	 * 输出纯HTML
	 */
	public static FullHttpResponse renderHTML(byte[] html) {
		return render(html, CONTENT_TYPE_HTML);
	}

	public static FullHttpResponse getNotFoundResponse(){
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"404.vm",null);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}

	public static FullHttpResponse getServerErrorResponse(){
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"500.vm",null);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}

	public static FullHttpResponse getErrorResponse(String errorMessage){
		Map<String,Object> contentMap = new HashMap<String,Object>(1);
		contentMap.put("errorMessage",errorMessage);
		String content = getPageContent(CommonConstants.BASE_VIEW_PATH+"error.vm",contentMap);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}

	public static String getPageContent(String templateName,Map<String, Object> contentMap){
		try {
			return VelocityUtil.parse(templateName,contentMap);
		} catch (Exception e) {
			logger.error("getPageContent Error,cause:",e);
		}
		return CommonConstants.SERVER_INTERNAL_ERROR_DESC;
	}

	public static byte[] getBytes(Object content){
		if(content==null){
			return EMPTY_CONTENT.getBytes(CharsetUtil.UTF_8);
		}
		String data = content.toString();
		data = (data==null || data.trim().length()==0)?EMPTY_CONTENT:data;
		return data.getBytes(CharsetUtil.UTF_8);
	}

	/**
	 * response输出
	 * @param bytes
	 * @param contentType
	 */
	public static FullHttpResponse render(byte[] bytes, String contentType){
		if(bytes == null){
			bytes = HttpRenderUtil.getBytes(EMPTY_CONTENT);
		}
		ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
		if(contentType!=null && contentType.trim().length()>0) {
			response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
		response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
		return response;
	}

}
