package com.redant.core.common.constants;

import com.redant.core.common.util.PropertiesUtil;

/**
 * 公共常量
 * @author houyi.wh
 * @date 2017-10-20
 */
public class CommonConstants {


    private static final String REDANT_PROPERTIES_PATH = "/redant.properties";

    private static PropertiesUtil propertiesUtil = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH);

    /**
     * 服务端口号
     */
    public static final int SERVER_PORT = propertiesUtil.getInt("netty.server.port",8888);

    /**
     * BossGroup Size
     * 先从启动参数中获取：-Dnetty.server.bossGroup.size=2
     * 如果获取不到从配置文件中获取
     * 如果再获取不到则取默认值
     */
    public static final int BOSS_GROUP_SIZE = null!=Integer.getInteger("netty.server.bossGroup.size")?Integer.getInteger("netty.server.bossGroup.size"):propertiesUtil.getInt("netty.server.bossGroup.size",2);

    /**
     * WorkerGroup Size
     * 先从启动参数中获取：-Dnetty.server.workerGroup.size=4
     * 如果获取不到从配置文件中获取
     * 如果再获取不到则取默认值
     */
    public static final int WORKER_GROUP_SIZE = null!=Integer.getInteger("netty.server.workerGroup.size")?Integer.getInteger("netty.server.workerGroup.size"):propertiesUtil.getInt("netty.server.workerGroup.size",4);

    /**
     * 能处理的最大数据的字节数
     */
    public static final int MAX_CONTENT_LENGTH = propertiesUtil.getInt("netty.maxContentLength",10485760);

    /**
     * 是否开启ssl
     */
    public static final boolean USE_SSL = propertiesUtil.getBoolean("netty.server.use.ssl");

    /**
     * KeyStore path
     */
    public static final String KEY_STORE_PATH = propertiesUtil.getString("ssl.keyStore.path");

    /**
     * KeyStore password
     */
    public static final String KEY_STORE_PASSWORD = propertiesUtil.getString("ssl.keyStore.password");

    /**
     * 扫描bean的包路径
     */
    public static final String BEAN_SCAN_PACKAGE = propertiesUtil.getString("bean.scan.package");

    /**
     * 扫描interceptor的包路径
     */
    public static final String INTERCEPTOR_SCAN_PACKAGE = propertiesUtil.getString("interceptor.scan.package");

    /**
     * 服务端出错时的错误描述
     */
    public static final String SERVER_INTERNAL_ERROR_DESC = propertiesUtil.getString("server.internal.error.desc");

    public static final String FAVICON_ICO = "/favicon.ico";

    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";

    public static final String CONNECTION_CLOSE = "close";

}
