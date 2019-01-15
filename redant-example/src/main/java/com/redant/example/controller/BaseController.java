package com.redant.example.controller;


import com.redant.core.common.enums.RequestMethod;
import com.redant.core.common.html.DefaultHtmlMaker;
import com.redant.core.common.html.HtmlMaker;
import com.redant.core.common.html.HtmlMakerEnum;
import com.redant.core.common.html.HtmlMakerFactory;
import com.redant.core.common.util.HtmlContentUtil;
import com.redant.core.common.view.PageIndex;
import com.redant.core.controller.annotation.Controller;
import com.redant.core.render.RenderType;
import com.redant.core.controller.annotation.Mapping;


/**
 * BaseController
 * @author houyi.wh
 * @date 2017-10-20
 */
@Controller(path="/")
public class BaseController {

    @Mapping(requestMethod=RequestMethod.GET,renderType=RenderType.HTML)
    public String index(){
        HtmlMaker htmlMaker = HtmlMakerFactory.instance().build(HtmlMakerEnum.STRING,DefaultHtmlMaker.class);
        String htmlTpl = PageIndex.HTML;
        return HtmlContentUtil.getPageContent(htmlMaker, htmlTpl,null);
    }

}
