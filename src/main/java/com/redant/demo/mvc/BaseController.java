package com.redant.demo.mvc;


import com.redant.common.constants.CommonConstants;
import com.redant.common.enums.RequestMethod;
import com.redant.core.render.DefaultRender;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import com.redant.common.util.HttpRenderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * BaseController
 * @author gris.wang
 * @create 2017-10-20
 */
@RouterController(path="/")
public class BaseController {

    private final static Logger logger = LoggerFactory.getLogger(BaseController.class);


    @RouterMapping(requestMethod=RequestMethod.GET,renderType=RenderType.HTML)
    public DefaultRender index(){
        String content = HttpRenderUtil.getPageContent(CommonConstants.BASE_VIEW_PATH+"index.vm",null);
        return new DefaultRender(RenderType.HTML,content);
    }



}
