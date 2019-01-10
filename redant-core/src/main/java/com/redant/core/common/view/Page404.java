package com.redant.core.common.view;


import cn.hutool.core.util.StrUtil;

/**
 * @author gris.wang
 * @date 2017/12/1
 **/
public class Page404 {

    private Page404(){

    }

    public static final String HTML;

    static{
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>").append(StrUtil.CRLF)
                .append("<html lang=\"en\">").append(StrUtil.CRLF)
                .append("<head>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<meta charset=\"UTF-8\">").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<title>404-Resource Not Found</title>").append(StrUtil.CRLF)
                .append("</head>").append(StrUtil.CRLF)
                .append("<body>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<div>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append(StrUtil.TAB).append("Resource Not Found!").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("</div>").append(StrUtil.CRLF)
                .append("</body>").append(StrUtil.CRLF)
                .append("</html>");
        HTML = sb.toString();
    }

}
