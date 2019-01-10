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

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRender.class);

	public RenderType renderType;

	private Object content;

	protected FullHttpResponse response;

	public DefaultRender(RenderType renderType, Object content){
		this.renderType = renderType;
		this.content = content;
	}

	@Override
	public FullHttpResponse response(){
		if(response==null) {
			switch (renderType) {
				case JSON:
					response = HttpRenderUtil.renderJSON(content);
					break;
				case TEXT:
					response = HttpRenderUtil.renderText(content);
					break;
				case XML:
					response = HttpRenderUtil.renderXML(content);
					break;
				case HTML:
					response = HttpRenderUtil.renderHTML(content);
					break;
				default:
					response = HttpRenderUtil.getServerErrorResponse();
					LOGGER.error("unknown response type");
			}
		}
		return response;
	}


}
