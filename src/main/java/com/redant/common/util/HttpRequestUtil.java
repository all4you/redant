package com.redant.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.common.enums.ContentType;
import com.redant.core.converter.PrimitiveTypeUtil;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gris.wang
 * @since 2017/11/16
 **/
public class HttpRequestUtil {

    /**
     * 获取请求参数的Map
     * @param request
     * @return
     */
    public static Map<String, List<String>> getParameterMap(HttpRequest request){
        Map<String, List<String>> paramMap = new HashMap<>();

        HttpMethod method = request.method();
        if(HttpMethod.GET.equals(method)){
            String uri = request.uri();
            QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, CharsetUtil.UTF_8);
            paramMap = queryDecoder.parameters();

        }else if(HttpMethod.POST.equals(method)){
            FullHttpRequest fullRequest = (FullHttpRequest) request;
            paramMap = getPostParamMap(fullRequest);
        }

        return paramMap;
    }


    /**
     * 获取post请求的参数map
     * 目前支持最常用的 application/json 、application/x-www-form-urlencoded 几种 POST Content-type，可自行扩展！！！
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<String>> getPostParamMap(FullHttpRequest fullRequest) {
        Map<String, List<String>> paramMap = new HashMap<>();
        HttpHeaders headers = fullRequest.headers();
        String contentType = getContentType(headers);
        if(ContentType.APPLICATION_JSON.toString().equals(contentType)){
            String jsonStr = fullRequest.content().toString(CharsetUtil.UTF_8);
            JSONObject obj = JSON.parseObject(jsonStr);
            for(Map.Entry<String, Object> item : obj.entrySet()){
                String key = item.getKey();
                Object value = item.getValue();
                Class<?> valueType = value.getClass();

                List<String> valueList;
                if(paramMap.containsKey(key)){
                    valueList = paramMap.get(key);
                }else{
                    valueList = new ArrayList<String>();
                }

                if(PrimitiveTypeUtil.isPriType(valueType)){
                    valueList.add(value.toString());
                    paramMap.put(key, valueList);

                }else if(valueType.isArray()){
                    int length = Array.getLength(value);
                    for(int i=0; i<length; i++){
                        String arrayItem = String.valueOf(Array.get(value, i));
                        valueList.add(arrayItem);
                    }
                    paramMap.put(key, valueList);

                }else if(List.class.isAssignableFrom(valueType)){
                    if(valueType.equals(JSONArray.class)){
                        JSONArray jArray = JSONArray.parseArray(value.toString());
                        for(int i=0; i<jArray.size(); i++){
                            valueList.add(jArray.getString(i));
                        }
                    }else{
                        valueList = (ArrayList<String>) value;
                    }
                    paramMap.put(key, valueList);

                }else if(Map.class.isAssignableFrom(valueType)){
                    Map<String, String> tempMap = (Map<String, String>) value;
                    for(Map.Entry<String, String> entry : tempMap.entrySet()){
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(entry.getValue());
                        paramMap.put(entry.getKey(), tempList);
                    }
                }
            }

        }else if(ContentType.APPLICATION_FORM_URLENCODED.toString().equals(contentType)){
            String jsonStr = fullRequest.content().toString(CharsetUtil.UTF_8);
            QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
            paramMap = queryDecoder.parameters();
        }

        return paramMap;
    }

    /**
     * 获取contentType
     * @param headers
     * @return
     */
    public static String getContentType(HttpHeaders headers){
        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        String[] list = contentType.split(";");
        return list[0];
    }

}
