package com.redant.common.util;

import com.redant.common.constants.CommonConstants;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用Velocity生成内容的工具类. 
 *  
 */  
public class VelocityUtil {

    private static Logger logger = LoggerFactory.getLogger(VelocityUtil.class);

    private static final String FILE_RESOURCE_LOADER_CLASS = "file.resource.loader.class";

    private static VelocityEngine velocityEngine;

    static {  
        try {  
            Velocity.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
            Velocity.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
            Velocity.setProperty(RuntimeConstants.COUNTER_NAME, "c");
            Velocity.init();  
        } catch (Exception e) {
            logger.error("Exception occurs while initialize the Velociy.", e);
        }  
    }  
  
    /** 
     * 基于模板内容渲染得出结果
     *  
     * @param templateContent
     *            模板内容. 
     * @param content
     *            模板数据.
     * @throws IOException  
     */  
    public static String render(String templateContent,Map<String, ?> content) throws Exception{
        VelocityContext context = new VelocityContext(content);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "", templateContent);
        return writer.toString();
    }
  
    private static VelocityEngine velocityEngine() {
        if(velocityEngine==null){
            velocityEngine = new VelocityEngine();
            // 从classpath读取模板文件
            velocityEngine.setProperty(FILE_RESOURCE_LOADER_CLASS,"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
            velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
            velocityEngine.setProperty(RuntimeConstants.COUNTER_NAME, "c");
            try {
                velocityEngine.init();
            } catch (Exception e) {
                logger.error("get velocityEngine error,cause:",e);
            }
        }
        return velocityEngine;
    }


    /**
     * 基于模板文件得到转换后的结果
     * @param templateName
     * @param contentMap
     * @return
     */
    public static String parse(String templateName,Map<String, Object> contentMap) throws Exception{
        VelocityContext context = new VelocityContext(contentMap);
        Template template = velocityEngine().getTemplate(templateName);
        StringWriter writer = new StringWriter();
        if(template != null) {
            template.merge(context, writer);
        }
        writer.flush();
        writer.close();
        return writer.toString();
    }
  
    public static void main(String[] args) {  
        Map<String, Object> contentMap = new HashMap<String, Object>();
        try {
            logger.info(parse(CommonConstants.BASE_VIEW_PATH+"404.vm",contentMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}