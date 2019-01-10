package com.redant.core.router;

import com.redant.core.invocation.ControllerProxy;
import com.redant.core.render.RenderType;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 路由上下文
 * @author gris.wang
 * @date 2017-10-20
 */
public class RouterContext {

    private static final RouterHolder ROUTER_HOLDER = RouterHolder.create();

    /**
     * 初始化路由
     */
    public static void initRouters(){
        ROUTER_HOLDER.initRouters();
    }

    /**
     * 获取路由结果
     */
    public static RouteResult<RenderType> getRouteResult(HttpMethod method, String uri){
        return ROUTER_HOLDER.getRouteResult(method, uri);
    }

    /**
     * 根据routeResult获取ControllerProxy
     */
    public static ControllerProxy getControllerProxy(RouteResult<?> routeResult){
        return ROUTER_HOLDER.getControllerProxy(routeResult);
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
