package com.redant.core.handler;

import com.redant.core.TemporaryDataHolder;
import com.redant.core.common.constants.CommonConstants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求分发控制器
 * @author houyi.wh
 * @date 2017-10-20
 */
public class TemporaryDataStorer extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(TemporaryDataStorer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            if(!CommonConstants.FAVICON_ICO.equals(request.uri())){
                // 将request和context存储到ThreadLocal中去，便于后期在其他地方获取并使用
                TemporaryDataHolder.storeHttpRequest(request);
                TemporaryDataHolder.storeContext(ctx);
            }
        }
        /*
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.error("ctx close,cause:",cause);
    }


}
