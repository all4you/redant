package com.redant.common.html;

import java.util.Map;

/**
 * html生成器
 * @author gris.wang
 * @since 2017/12/1
 **/
public interface HtmlMaker {

    /**
     * 根据html模板生成html内容
     * @param htmlTemplate
     * @param contentMap
     * @return
     */
    String make(String htmlTemplate,Map<String, Object> contentMap);


}
