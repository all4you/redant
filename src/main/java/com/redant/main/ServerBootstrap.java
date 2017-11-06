package com.redant.main;

import com.mybatissist.config.Config;
import com.redant.core.bean.BeanContext;
import com.redant.core.server.NettyServer;
import com.redant.core.router.RouterContext;
import com.mybatissist.sqlsession.SqlSessionContext;

/**
 * 服务端启动入口
 * @author gris.wang
 * @create 2017-10-20
 */
public class ServerBootstrap {

    public static void main(String[] args) {

        BeanContext.initBeans();
        RouterContext.initRouters();
        SqlSessionContext.buildFactory();
        Config.instance().printSql(true);

        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }

}
