package com.redant.common.serialize;

import com.alibaba.fastjson.JSON;
import com.redant.core.bean.annotation.Bean;
import io.netty.util.CharsetUtil;

@Bean(name="jsonSerializer")
public class JSONSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        String data = JSON.toJSONString(obj);
        return data.getBytes(CharsetUtil.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        String data = new String(bytes,CharsetUtil.UTF_8);
        return (T)JSON.parse(data);
    }

}
