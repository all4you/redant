package com.redant.core.server;

import com.redant.core.common.constants.CommonConstants;
import com.redant.core.handler.ControllerDispatcher;
import com.redant.core.handler.ResponseWriter;
import com.redant.core.handler.ssl.SslContextHelper;
import com.redant.core.init.InitExecutor;
import com.redant.core.interceptor.InterceptorUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;


/**
 * NettyHttpServer
 * @author houyi.wh
 * @date 2017-10-20
 */
public final class NettyHttpServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServer.class);

    @Override
    public void preStart() {
        InitExecutor.init();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(CommonConstants.BOSS_GROUP_SIZE, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(CommonConstants.WORKER_GROUP_SIZE, new DefaultThreadFactory("worker", true));
        try {
            long start = System.currentTimeMillis();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ServerInitializer());

            ChannelFuture future = b.bind(CommonConstants.SERVER_PORT).sync();
            long cost = System.currentTimeMillis()-start;
            LOGGER.info("[NettyHttpServer] Startup at port:{} cost:{}[ms]",CommonConstants.SERVER_PORT,cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("[NettyHttpServer] InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class ServerInitializer extends ChannelInitializer<SocketChannel> {

        static final Logger logger = LoggerFactory.getLogger(ServerInitializer.class);

        /**
         * 业务线程池
         * 用以单独处理业务handler，避免造成IO线程的阻塞
         * TODO 是否需要使用业务线程池，线程池的数量该怎么确定
         * private static final EventExecutorGroup EVENT_EXECUTOR = new DefaultEventExecutorGroup(50);
         */

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            if(CommonConstants.USE_SSL){
                SslContext context = SslContextHelper.getSslContext(CommonConstants.KEY_STORE_PATH,CommonConstants.KEY_STORE_PASSWORD);
                if(context!=null) {
                    SSLEngine engine = context.newEngine(ch.alloc());
                    engine.setUseClientMode(false);
                    pipeline.addLast(new SslHandler(engine));
                }else{
                    logger.warn("SslContext is null with keyPath={}",CommonConstants.KEY_STORE_PATH);
                }
            }

            // HttpServerCodec is a combination of HttpRequestDecoder and HttpResponseEncoder
            // 使用HttpServerCodec将ByteBuf编解码为httpRequest/httpResponse
            pipeline.addLast(new HttpServerCodec());
            // add gizp compressor for http response content
            pipeline.addLast(new HttpContentCompressor());
            // 将多个HttpRequest组合成一个FullHttpRequest
            pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
            pipeline.addLast(new ChunkedWriteHandler());
            // 前置拦截器
            ChannelHandler[] preInterceptors = InterceptorUtil.getPreInterceptors();
            if(preInterceptors.length>0) {
                pipeline.addLast(preInterceptors);
            }
            // 路由分发器
            pipeline.addLast(new ControllerDispatcher());
            // 后置拦截器
            ChannelHandler[] afterInterceptors = InterceptorUtil.getAfterInterceptors();
            if(afterInterceptors.length>0) {
                pipeline.addLast(afterInterceptors);
            }
            // 请求结果响应
            pipeline.addLast(new ResponseWriter());
        }
    }

}