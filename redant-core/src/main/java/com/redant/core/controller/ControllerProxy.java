package com.redant.core.controller;

import com.redant.core.common.enums.RequestMethod;
import com.redant.core.render.RenderType;

import java.lang.reflect.Method;

/**
 * 路由请求代理，用以根据路由调用具体的controller类
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ControllerProxy {

    private RenderType renderType;

    private RequestMethod requestMethod;

    private Object controller;

    private Method method;

    private String methodName;

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "{requestMethod:"+requestMethod+",controller:"+controller.getClass().getName()+",methodName:"+methodName+"}";
    }
}
