package com.redant.common.util;

import com.redant.common.constants.CommonConstants;
import com.redant.common.html.HtmlMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author gris.wang
 * @since 2017/12/1
 **/
public class HtmlContentUtil {

    private final static Logger logger = LoggerFactory.getLogger(HtmlContentUtil.class);

    private HtmlContentUtil(){

    }

    /**
     * 获取页面内容
     * @param htmlMaker
     * @param htmlTemplate
     * @param contentMap
     * @return
     */
    public static String getPageContent(HtmlMaker htmlMaker,String htmlTemplate,Map<String, Object> contentMap){
        try {
            return htmlMaker.make(htmlTemplate,contentMap);
        } catch (Exception e) {
            logger.error("getPageContent Error,cause:",e);
        }
        return CommonConstants.SERVER_INTERNAL_ERROR_DESC;
    }


}
