package com.redant.core.handler;

import com.redant.common.constants.CommonConstants;
import com.redant.core.DataHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

/**
 * 将请求数据存储起来
 * @author gris.wang
 * @since 2017/11/7
 */
public class DataStoreHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            if(!request.uri().equals(CommonConstants.FAVICON_ICO)){
                // 将request和context存储到ThreadLocal中去，便于后期在其他地方获取并使用
                DataHolder.store(DataHolder.HolderType.REQUEST,request);
                DataHolder.store(DataHolder.HolderType.CONTEXT,ctx);
            }
        }
        /**
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

}
