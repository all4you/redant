package com.redant.common.codec;

import com.redant.core.bean.BeanContext;
import com.redant.common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Neko Rpc Decoder
 * @author hwang
 * @version 0.1
 */
public class ProtostuffDecoder extends ByteToMessageDecoder {

	private static final Log logger = LogFactory.getLog(ProtostuffDecoder.class);

	private Serializer serializer;
	
    private Class<?> genericClass;

    public ProtostuffDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    	if(null==serializer){
    		logger.info("get serializer{"+serializer+"} from bean context");
    		serializer = BeanContext.getBean("protostuffSerializer", Serializer.class);
    	}
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        // 判断字节流的长度是否与内容一致，防止TCP粘包
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(serializer.deserialize(data, genericClass));
    }
  
    
}
