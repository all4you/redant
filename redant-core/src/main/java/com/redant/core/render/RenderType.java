package com.redant.core.render;

/**
 * 返回的响应类型
 * @author houyi.wh
 * @date 2017-10-20
 */
public enum RenderType {

	/**
	 * JSON
	 */
	JSON("application/json;charset=UTF-8"),
	/**
	 * XML
	 */
	XML("text/xml;charset=UTF-8"),
	/**
	 * TEXT
	 */
	TEXT("text/plain;charset=UTF-8"),
	/**
	 * HTML
	 */
	HTML("text/html;charset=UTF-8");

	private String contentType;

	RenderType(String contentType){
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}
}
