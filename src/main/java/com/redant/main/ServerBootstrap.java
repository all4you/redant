package com.redant.main;

import com.mybatissist.config.MybatissistConfig;
import com.redant.core.bean.BeanContext;
import com.redant.core.server.NettyServer;
import com.redant.core.router.RouterContext;
import com.mybatissist.sqlsession.SqlSessionContext;

/**
 * 服务端启动入口
 * @author gris.wang
 * @create 2017-10-20
 */
public final class ServerBootstrap {

    public static void main(String[] args) {

        BeanContext.initBeans();
        RouterContext.initRouters();
        MybatissistConfig.instance().printSql(true);
        SqlSessionContext.buildFactory();

        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }

}
