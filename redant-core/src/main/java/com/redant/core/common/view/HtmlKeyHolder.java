package com.redant.core.common.view;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
public class HtmlKeyHolder {

    private HtmlKeyHolder(){

    }

    /**
     * 未转义
     */
    public static final String START_NO_ESCAPE = "#[";

    /**
     * 对[转义
     */
    public static final String START_ESCAPE = "#\\[";

    public static final String END = "]";

}
