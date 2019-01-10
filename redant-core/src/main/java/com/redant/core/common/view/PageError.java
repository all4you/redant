package com.redant.core.common.view;

import cn.hutool.core.util.StrUtil;

/**
 * @author gris.wang
 * @date 2017/12/1
 **/
public class PageError {

    private PageError(){

    }

    public static final String HTML;

    static{
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>").append(StrUtil.CRLF)
                .append("<html lang=\"en\">").append(StrUtil.CRLF)
                .append("<head>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<meta charset=\"UTF-8\">").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<title>Error Occur</title>").append(StrUtil.CRLF)
                .append("</head>").append(StrUtil.CRLF)
                .append("<body>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("<div>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append(StrUtil.TAB).append("<p>").append("Error Occur,Cause:").append("</p>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append(StrUtil.TAB).append("<p>").append(HtmlKeyHolder.START_NO_ESCAPE+"errorMessage"+HtmlKeyHolder.END).append("</p>").append(StrUtil.CRLF)
                .append(StrUtil.TAB).append("</div>").append(StrUtil.CRLF)
                .append("</body>").append(StrUtil.CRLF)
                .append("</html>");
        HTML = sb.toString();
    }

}
