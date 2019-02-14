package com.redant.example.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.redant.core.anno.Order;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.context.RedantContext;
import com.redant.core.interceptor.Interceptor;
import com.redant.core.render.RenderType;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 该拦截器可以计算出用户自定义 Controller 方法的执行时间，并且当请求的参数中有 block=true 时，就会被拦截
 * @author houyi
 **/
@Order(value = 2)
public class InterceptorExample extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorExample.class);

    private long start;

    @Override
    public boolean preHandle(Map<String, List<String>> paramMap) {
        start = System.currentTimeMillis();
        if(CollectionUtil.isNotEmpty(paramMap)) {
            List<String> values = paramMap.get("block");
            if(CollectionUtil.isNotEmpty(values)){
                String val = values.get(0);
                if("true".equals(val)){
                    JSONObject content = new JSONObject();
                    content.put("status","你被前置拦截器拦截了");
                    content.put("reason","请求参数中有 block=true");
                    FullHttpResponse response = HttpRenderUtil.render(content, RenderType.JSON);
                    RedantContext.currentContext().setResponse(response);
                    return false;
                }
            }
        }
        return super.preHandle(paramMap);
    }

    @Override
    public void afterHandle(Map<String, List<String>> paramMap) {
        long end = System.currentTimeMillis();
        long cost = end - start;
        LOGGER.info("cost:{}[ms]",cost);
    }

}
