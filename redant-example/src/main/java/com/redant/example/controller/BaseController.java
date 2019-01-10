package com.redant.example.controller;


import com.redant.core.common.enums.RequestMethod;
import com.redant.core.common.html.DefaultHtmlMaker;
import com.redant.core.common.html.HtmlMakerEnum;
import com.redant.core.common.html.HtmlMakerFactory;
import com.redant.core.common.util.HtmlContentUtil;
import com.redant.core.common.view.PageIndex;
import com.redant.core.render.DefaultRender;
import com.redant.core.render.Render;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;


/**
 * BaseController
 * @author gris.wang
 * @date 2017-10-20
 */
@RouterController(path="/")
public class BaseController {

    @RouterMapping(requestMethod=RequestMethod.GET,renderType=RenderType.HTML)
    public Render index(){
        String html = HtmlContentUtil.getPageContent(HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class),PageIndex.HTML,null);
        return new DefaultRender(RenderType.HTML,html);
    }

}
