package com.redant.common.util;

import com.redant.common.html.DefaultHtmlMaker;
import com.redant.common.html.HtmlMakerEnum;
import com.redant.common.html.HtmlMakerFactory;
import com.redant.view.Page404;
import com.redant.view.Page500;
import com.redant.view.PageError;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpRenderUtil
 * @author gris.wang
 * @create 2017-10-20
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


	/**
	 * 404NotFoundResponse
	 * @return
	 */
	public static FullHttpResponse getNotFoundResponse(){
		String content = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),Page404.HTML,null);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}

	/**
	 * ServerErrorResponse
	 * @return
	 */
	public static FullHttpResponse getServerErrorResponse(){
		String content = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),Page500.HTML,null);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}

	/**
	 * ErrorResponse
	 * @param errorMessage
	 * @return
	 */
	public static FullHttpResponse getErrorResponse(String errorMessage){
		Map<String,Object> contentMap = new HashMap<String,Object>(1);
		contentMap.put("errorMessage",errorMessage);
		String content = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),PageError.HTML,contentMap);
		return render(getBytes(content), CONTENT_TYPE_HTML);
	}


	/**
	 * 转换byte
	 * @param content
	 * @return
	 */
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
