package com.redant.cluster.master;

import com.redant.cluster.service.discover.Discovery;
import com.redant.common.constants.CommonConstants;
import com.redant.common.util.HttpRenderUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * MasterServerHandler
 * @author gris.wang
 * @since 2017/11/20
 */
public class MasterServerHandler extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(MasterServerHandler.class);

    private HttpRequest request;
    private Channel channel;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof HttpRequest){
            request = (HttpRequest)msg;
            if(request.uri().equals(CommonConstants.FAVICON_ICO)){
                return;
            }
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            channel = ctx.channel();

            FullHttpResponse response = null;
            try {
                MasterClient client = new MasterClient(Discovery.nextSlave());
                response = client.sendRequest(request);
            }catch(Exception e){
                logger.error("MasterServerHandler error,cause:",e);
            }
            if(response==null){
                logger.warn("response is null");
                response = HttpRenderUtil.render(null,HttpRenderUtil.CONTENT_TYPE_TEXT);
            }
            writeResponse(response);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("MasterServerHandler ctx close,cause:",cause);
    }


    /**
     * 响应消息
     */
    private void writeResponse(FullHttpResponse response){
        boolean close = isClose();
        if(!close && !response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)){
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        }
        ChannelFuture future = channel.writeAndFlush(response);
        if(close){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isClose(){
        return request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_KEEP_ALIVE, true));
    }

}
