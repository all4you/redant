package com.redant.cluster.master;

import com.redant.cluster.slave.SlaveNode;
import com.redant.common.constants.CommonConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送HttpRequest到Slave获得响应
 * @author gris.wang
 * @since 2017/11/20
 **/
public class MasterClient extends SimpleChannelInboundHandler<HttpResponse> {

    private final static Logger logger = LoggerFactory.getLogger(MasterClient.class);

    private final String HOST;

    private final int PORT;

    private HttpResponse httpResponse;

    public MasterClient(SlaveNode slaveNode){
        if(slaveNode==null){
            throw new IllegalArgumentException("slaveNode is null");
        }
        this.HOST = slaveNode.getHost();
        this.PORT = slaveNode.getPort();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpResponse httpResponse) throws Exception {
        this.httpResponse = httpResponse;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("MasterClient caught exception", cause);
        ctx.close();
    }


    /**
     * 发送http请求到slave
     * @param request
     * @return
     * @throws Exception
     */
    public HttpResponse sendRequest(HttpRequest request) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 客户端 Bootstrap 对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    pipeline.addLast(new HttpResponseDecoder());
                    pipeline.addLast(new HttpContentDecompressor());
                    pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    pipeline.addLast(new HttpRequestEncoder());
                    pipeline.addLast(MasterClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 slave 服务器
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            Channel channel = future.channel();
            // 将HttpRequest对象发送至SlaveServer，等待处理
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            // 返回 Slave 响应对象
            return httpResponse;
        } catch (InterruptedException e) {
            logger.error("sendRequest error,cause:",e);
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }

}
