package com.redant.core.common.util;

import com.redant.core.common.html.HtmlMaker;
import com.redant.core.common.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
public class HtmlContentUtil {

    private final static Logger logger = LoggerFactory.getLogger(HtmlContentUtil.class);

    private HtmlContentUtil(){

    }

    /**
     * 获取页面内容
     * @param htmlMaker htmlMaker
     * @param htmlTemplate html模板
     * @param contentMap 参数
     * @return 页面内容
     */
    public static String getPageContent(HtmlMaker htmlMaker, String htmlTemplate, Map<String, Object> contentMap){
        try {
            return htmlMaker.make(htmlTemplate,contentMap);
        } catch (Exception e) {
            logger.error("getPageContent Error,cause:",e);
        }
        return CommonConstants.SERVER_INTERNAL_ERROR_DESC;
    }


}
