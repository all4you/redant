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
public class MasterClient extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MasterClient.class);

    private final String HOST;

    private final int PORT;

    private FullHttpResponse httpResponse;

    public MasterClient(SlaveNode slaveNode){
        if(slaveNode==null){
            throw new IllegalArgumentException("slaveNode is null");
        }
        this.HOST = slaveNode.getHost();
        this.PORT = slaveNode.getPort();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpResponse) {
            this.httpResponse = (FullHttpResponse)msg;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("MasterClient caught exception", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    /**
     * 发送http请求到slave
     * @param request
     * @return
     * @throws Exception
     */
    public FullHttpResponse sendRequest(HttpRequest request) {
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
                    // 客户端发送的是httpRequest，所以要使用HttpRequestEncoder进行编码
                    pipeline.addLast(new HttpRequestEncoder());
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    pipeline.addLast(new HttpResponseDecoder());
                    pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
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
            return this.httpResponse;
        } catch (InterruptedException e) {
            logger.error("sendRequest error,cause:",e);
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }

}
