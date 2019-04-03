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
 * 当请求的参数中有 block=true 时，就会被该拦截器拦截
 * @author houyi
 **/
@Order(value = 1)
public class BlockInterceptor extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockInterceptor.class);

    @Override
    public boolean preHandle(Map<String, List<String>> paramMap) {
        if(CollectionUtil.isNotEmpty(paramMap)) {
            String blockKey = "block";
            String blockVal = "true";
            List<String> values = paramMap.get(blockKey);
            if(CollectionUtil.isNotEmpty(values)){
                String val = values.get(0);
                if(blockVal.equals(val)){
                    JSONObject content = new JSONObject();
                    content.put("status","你被前置方法拦截了");
                    content.put("reason","请求参数中有 block=true");
                    FullHttpResponse response = HttpRenderUtil.render(content, RenderType.JSON);
                    RedantContext.currentContext().setResponse(response);
                    LOGGER.info("[BlockInterceptor] blocked preHandle");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(Map<String, List<String>> paramMap) {
        // do nothing
    }

}
