package com.redant.view;

import com.xiaoleilu.hutool.util.StrUtil;

/**
 * @author gris.wang
 * @since 2017/12/1
 **/
public final class PageIndex {

    private PageIndex(){

    }

    public static final String HTML;

    static{
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html>").append(StrUtil.CRLF)
          .append("<html lang=\"en\">").append(StrUtil.CRLF)
          .append("<head>").append(StrUtil.CRLF)
          .append(StrUtil.TAB).append("<meta charset=\"UTF-8\">").append(StrUtil.CRLF)
          .append(StrUtil.TAB).append("<title>redant</title>").append(StrUtil.CRLF)
          .append("</head>").append(StrUtil.CRLF)
          .append("<body>").append(StrUtil.CRLF)
          .append(StrUtil.TAB).append("<div>").append(StrUtil.CRLF)
          .append(StrUtil.TAB).append(StrUtil.TAB).append("Welcome to redant!").append(StrUtil.CRLF)
          .append(StrUtil.TAB).append("</div>").append(StrUtil.CRLF)
          .append("</body>").append(StrUtil.CRLF)
          .append("</html>");
        HTML = sb.toString();
    }

}
