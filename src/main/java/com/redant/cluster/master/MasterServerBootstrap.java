package com.redant.cluster.master;

/**
 * MasterServerBootstrap
 * @author gris.wang
 * @since 2017/11/20
 **/
public class MasterServerBootstrap {

    public static void main(String[] args) {
        // 注册MasterServer到ZK

        // 启动MasterServer
        MasterServer server = new MasterServer();
        server.start();

    }
}
