package com.redant.core.serialize;

import com.alibaba.fastjson.JSON;
import com.redant.core.bean.annotation.Bean;

@Bean(name="jsonSerializer")
public class JSONSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        String data = JSON.toJSONString(obj);
        return data.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        String data = new String(bytes);
        return (T)JSON.parse(data);
    }

}
