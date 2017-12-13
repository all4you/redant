package com.redant.core.router;

import com.redant.core.invocation.ControllerProxy;
import com.redant.core.render.RenderType;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路由上下文
 * @author gris.wang
 * @create 2017-10-20
 */
public class RouterContext {

    private static final RouterHolder routerHolder = RouterHolder.create();

    /**
     * 初始化路由
     */
    public static void initRouters(){
        routerHolder.initRouters();
    }

    /**
     * 获取路由结果
     * @param method
     * @param uri
     * @return
     */
    public static RouteResult<RenderType> getRouteResult(HttpMethod method, String uri){
        return routerHolder.getRouteResult(method, uri);
    }

    /**
     * 根据routeResult获取ControllerProxy
     * @param routeResult
     * @return
     */
    public static ControllerProxy getControllerProxy(RouteResult<?> routeResult){
        return routerHolder.getControllerProxy(routeResult);
    }


    public static void main(String[] args) {
        int loopTimes = 200;

        class Runner implements Runnable{

            private Logger logger = LoggerFactory.getLogger(Runner.class);

            @Override
            public void run() {
                RouteResult<RenderType> result = RouterContext.getRouteResult(HttpMethod.GET,"/user/info");
                logger.info("routeResult={},currentThread={}",result,Thread.currentThread().getName());
            }
        }

        for(int i=0;i<loopTimes;i++){
            new Thread(new Runner()).start();
        }

    }


}
