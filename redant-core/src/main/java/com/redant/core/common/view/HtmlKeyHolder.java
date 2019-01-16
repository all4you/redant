package com.redant.core.common.view;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
public interface HtmlKeyHolder {

    /**
     * 未转义
     */
    String START_NO_ESCAPE = "#[";

    /**
     * 对[转义
     */
    String START_ESCAPE = "#\\[";

    String END = "]";

}
