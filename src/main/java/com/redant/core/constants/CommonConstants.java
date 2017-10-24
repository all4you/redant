package com.redant.core.constants;

import com.redant.core.util.PropertiesUtil;

public class CommonConstants {



    /**
     * 服务端口号
     */
    public static final int SERVER_PORT = PropertiesUtil.getInstance("/redant.properties").getInt("netty.server.port",8888);


    /**
     * 扫描bean的包路径
     */
    public static final String BEAN_SCAN_PACKAGE = PropertiesUtil.getInstance("/redant.properties").getString("bean.scan.package");


    /**
     * 视图文件根路径
     */
    public static final String BASE_VIEW_PATH = PropertiesUtil.getInstance("/redant.properties").getString("base.view.path");


    /**
     * 服务端出错时的错误描述
     */
    public static final String SERVER_INTERNAL_ERROR_DESC = PropertiesUtil.getInstance("/redant.properties").getString("server.internal.error.desc");


}
