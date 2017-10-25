package com.redant.core.render;

import com.redant.common.util.HttpRenderUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的渲染类
 * @author gris.wang
 * @create 2017-10-20
 */
public class BaseRender implements Render {

	private static final Logger logger = LoggerFactory.getLogger(BaseRender.class);

	public RenderType renderType;

	private byte[] bytes;

	public BaseRender(RenderType renderType, Object content){
		this.renderType = renderType;
		this.bytes = convertBytes(content);
	}

	public byte[] convertBytes(Object content){
		return HttpRenderUtil.getBytes(content);
	}

	@Override
	public FullHttpResponse process() throws Exception {
		FullHttpResponse response;
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
			logger.error("unknown render type");
		}
		return response;
	}

}
