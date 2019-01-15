package com.redant.core.common.html;

import java.util.Map;

/**
 * html生成器
 * @author houyi.wh
 * @date 2017/12/1
 **/
public interface HtmlMaker {

    /**
     * 根据html模板生成html内容
     * @param htmlTemplate html模板
     * @param contentMap 参数
     * @return html内容
     */
    String make(String htmlTemplate,Map<String, Object> contentMap);


}
