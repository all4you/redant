package com.redant.core;

import com.mybatissist.sqlsession.SqlSessionContext;
import com.redant.core.bean.BeanContext;
import com.redant.core.router.RouterContext;

/**
 * 服务器初始化
 * @author gris.wang
 * @since 2017/11/20
 **/
public class ServerInitUtil {

    /**
     * 服务器初始化工作
     */
    public static void init(){
        BeanContext.initBeans();
        RouterContext.initRouters();
        SqlSessionContext.buildFactory();
    }

}
