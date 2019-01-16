package com.redant.core.common.view;

import cn.hutool.core.util.StrUtil;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
public final class Page500 {

    private Page500(){

    }

    public static final String HTML;

    static{
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>").append(StrUtil.CRLF)
                .append("<html lang=\"en\">").append(StrUtil.CRLF)
                .append("<head>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<meta charset=\"UTF-8\">").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<title>500-Server Internal Error</title>").append(StrUtil.CRLF)
                .append("</head>").append(StrUtil.CRLF)
                .append("<body>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<div>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append(StrUtil.TAB).append("Server Internal Error!").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("</div>").append(StrUtil.CRLF)
                .append("</body>").append(StrUtil.CRLF)
                .append("</html>");
        HTML = sb.toString();
    }

}
