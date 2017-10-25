package com.redant.common.codec;

import com.redant.core.bean.BeanContext;
import com.redant.common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Neko Rpc Encoder
 * @author hwang
 * @version 0.1
 */
@SuppressWarnings("rawtypes")
public class ProtostuffEncoder extends MessageToByteEncoder {

	private static final Log logger = LogFactory.getLog(ProtostuffEncoder.class);

	private Serializer serializer;

    private Class<?> genericClass;

    public ProtostuffEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    
    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    	if(null==serializer){
    		logger.info("get serializer{"+serializer+"} from bean context");
    		serializer = BeanContext.getBean("protostuffSerializer", Serializer.class);
    	}
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            // 写入字节流的长度，防止解码时出现TCP粘包的情况
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
  
}