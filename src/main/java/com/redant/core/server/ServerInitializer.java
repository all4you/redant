package com.redant.core.server;

import com.redant.common.constants.CommonConstants;
import com.redant.core.ControllerDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        // HttpServerCodec is a combination of HttpRequestDecoder and HttpResponseEncoder
        p.addLast(new HttpServerCodec());

        // add gizp compressor for http response content
        p.addLast(new HttpContentCompressor());

        p.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));

        p.addLast(new ChunkedWriteHandler());

        p.addLast(new ControllerDispatcher());

    }

}

