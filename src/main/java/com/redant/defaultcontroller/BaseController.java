package com.redant.defaultcontroller;


import com.redant.common.constants.CommonConstants;
import com.redant.common.enums.RequestMethod;
import com.redant.common.html.DefaultHtmlMaker;
import com.redant.common.html.HtmlMakerEnum;
import com.redant.common.html.HtmlMakerFactory;
import com.redant.common.util.HtmlContentUtil;
import com.redant.core.render.DefaultRender;
import com.redant.core.render.Render;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import com.redant.view.PageIndex;


/**
 * BaseController
 * @author gris.wang
 * @create 2017-10-20
 */
@RouterController(path="/")
public class BaseController {

    @RouterMapping(requestMethod=RequestMethod.GET,renderType=RenderType.HTML)
    public Render index(){
        String html = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),PageIndex.HTML,null);
        return new DefaultRender(RenderType.HTML,html);
    }



}
