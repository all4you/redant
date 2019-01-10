package com.redant.core.render;

import com.redant.core.common.util.HttpRenderUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础渲染类
 * @author gris.wang
 * @date 2017-10-20
 */
public class DefaultRender implements Render {

	private static final Logger logger = LoggerFactory.getLogger(DefaultRender.class);

	public RenderType renderType;

	private byte[] bytes;

	protected FullHttpResponse response;

	public DefaultRender(RenderType renderType, Object content){
		this.renderType = renderType;
		this.bytes = convertBytes(content);
	}


	public byte[] convertBytes(Object content){
		return HttpRenderUtil.getBytes(content);
	}

	@Override
	public FullHttpResponse response(){
		if(response==null) {
			switch (renderType) {
				case JSON:
					response = HttpRenderUtil.renderJSON(bytes);
					break;
				case TEXT:
					response = HttpRenderUtil.renderText(bytes);
					break;
				case XML:
					response = HttpRenderUtil.renderXML(bytes);
					break;
				case HTML:
					response = HttpRenderUtil.renderHTML(bytes);
					break;
				default:
					response = HttpRenderUtil.getServerErrorResponse();
					logger.error("unknown response type");
			}
		}
		return response;
	}


}
