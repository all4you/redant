package com.redant.core.common.util;

import com.alibaba.fastjson.JSONObject;
import com.redant.core.common.html.DefaultHtmlMaker;
import com.redant.core.common.html.HtmlMaker;
import com.redant.core.common.html.HtmlMakerEnum;
import com.redant.core.common.html.HtmlMakerFactory;
import com.redant.core.common.view.Page404;
import com.redant.core.render.RenderType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * HttpRenderUtil
 *
 * @author houyi.wh
 * @date 2017-10-20
 */
public class HttpRenderUtil {

    public static final String EMPTY_CONTENT = "";

    private HttpRenderUtil() {

    }

    /**
     * response输出
     *
     * @param content    内容
     * @param renderType 返回类型
     * @return 响应对象
     */
    public static FullHttpResponse render(Object content, RenderType renderType) {
        byte[] bytes = HttpRenderUtil.getBytes(content);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        RenderType type = renderType != null ? renderType : RenderType.JSON;
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, type.getContentType());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
        return response;
    }

    /**
     * 404NotFoundResponse
     *
     * @return 响应对象
     */
    public static FullHttpResponse getNotFoundResponse() {
        HtmlMaker htmlMaker = HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING, DefaultHtmlMaker.class);
        String htmlTpl = Page404.HTML;
        String content = HtmlContentUtil.getPageContent(htmlMaker, htmlTpl, null);
        return render(content, RenderType.HTML);
    }

    /**
     * ServerErrorResponse
     *
     * @return 响应对象
     */
    public static FullHttpResponse getServerErrorResponse() {
        JSONObject object = new JSONObject();
        object.put("code", 500);
        object.put("message", "Server Internal Error!");
        return render(object, RenderType.JSON);
    }

    /**
     * ErrorResponse
     *
     * @param errorMessage 错误信息
     * @return 响应对象
     */
    public static FullHttpResponse getErrorResponse(String errorMessage) {
        JSONObject object = new JSONObject();
        object.put("code", 300);
        object.put("message", errorMessage);
        return render(object, RenderType.JSON);
    }

    /**
     * BlockedResponse
     *
     * @return 响应对象
     */
    public static FullHttpResponse getBlockedResponse() {
        JSONObject object = new JSONObject();
        object.put("code", 1000);
        object.put("message", "Blocked by user defined interceptor");
        return render(object, RenderType.JSON);
    }

    /**
     * 转换byte
     *
     * @param content 内容
     * @return 响应对象
     */
    private static byte[] getBytes(Object content) {
        if (content == null) {
            return EMPTY_CONTENT.getBytes(CharsetUtil.UTF_8);
        }
        String data = content.toString();
        data = (data == null || data.trim().length() == 0) ? EMPTY_CONTENT : data;
        return data.getBytes(CharsetUtil.UTF_8);
    }

}
