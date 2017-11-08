package com.redant.common.constants;

import com.redant.common.util.PropertiesUtil;

/**
 * 公共常量
 * @author gris.wang
 * @since 2017-10-20
 */
public class CommonConstants {


    private static final String REDANT_PROPERTIES_PATH = "/redant.properties";

    /**
     * 服务端口号
     */
    public static final int SERVER_PORT = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH).getInt("netty.server.port",8888);


    /**
     * 能处理的最大数据的字节数
     */
    public static final int MAX_CONTENT_LENGTH = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH).getInt("netty.maxContentLength",10485760);


    /**
     * 扫描bean的包路径
     */
    public static final String BEAN_SCAN_PACKAGE = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH).getString("bean.scan.package");


    /**
     * 视图文件根路径
     */
    public static final String BASE_VIEW_PATH = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH).getString("base.view.path");


    /**
     * 服务端出错时的错误描述
     */
    public static final String SERVER_INTERNAL_ERROR_DESC = PropertiesUtil.getInstance(REDANT_PROPERTIES_PATH).getString("server.internal.error.desc");

    public static final String FAVICON_ICO = "/favicon.ico";

    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";

    public static final String CONNECTION_CLOSE = "close";

}
