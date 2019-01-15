package com.redant.core.controller.context;

import com.redant.core.controller.ControllerProxy;
import com.redant.core.render.RenderType;
import com.redant.core.router.RouteResult;
import com.redant.core.router.context.DefaultRouterContext;
import com.redant.core.router.context.RouterContext;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyi.wh
 * @date 2019-01-15
 */
public class DefaultControllerContext implements ControllerContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultControllerContext.class);

    /**
     * 保存所有的RouterController的代理类
     */
    private static Map<String,ControllerProxy> proxyMap;

    /**
     * 路由上下文
     */
    private static RouterContext routerContext;

    /**
     * RouterContext的实例(单例)
     */
    private volatile static DefaultControllerContext context;

    private DefaultControllerContext(){
        routerContext = DefaultRouterContext.getInstance();
        proxyMap = new ConcurrentHashMap<>();
    }

    public static ControllerContext getInstance(){
        if(context==null) {
            synchronized (DefaultControllerContext.class) {
                if(context==null) {
                    context = new DefaultControllerContext();
                }
            }
        }
        return context;
    }


    @Override
    public void addProxy(String path, ControllerProxy proxy) {
        proxyMap.putIfAbsent(path, proxy);
    }

    @Override
    public ControllerProxy getProxy(HttpMethod method, String uri) {
        RouteResult<RenderType> routeResult = routerContext.getRouteResult(method, uri);
        if(routeResult==null){
            return null;
        }
        // 获取代理
        ControllerProxy controllerProxy = proxyMap.get(routeResult.decodedPath());
        LOGGER.debug("\n=========================  getControllerProxy =========================" +
                     "\nmethod={}, uri={}" +
                     "\ncontrollerProxy={}" +
                     "\n=========================  getControllerProxy =========================",
                method, uri, controllerProxy);
        return controllerProxy;
    }


}