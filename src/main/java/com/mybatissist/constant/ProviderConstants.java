package com.mybatissist.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Provider调用时所需要的一些常量
 * @author gris.wang
 * @create 2017-10-20
 */
public class ProviderConstants {


    /**
     * 空格
     */
    public static final String SPACE = " ";

    /**
     * 属性左括号
     */
    public static final String PROP_LEFT = "#{";

    /**
     * 属性右括号
     */
    public static final String PROP_RIGHT = "}";

    /**
     * AS
     */
    public static final String AS = " as ";


    /**
     * 点号
     */
    public static final String DOT = ".";


    /**
     * selectOne方法的sql语句后面增加该limit
     */
    public static final String LIMIT_1 = "\nLIMIT 1";


    /**
     * 主键
     */
    public static final String PRIMARY_KEY = "id";

    /**
     * 丢弃的字段
     */
    public static final List<String> ABANDON_FIELDS =  Arrays.asList(new String[]{
            "SERIALVERSIONUID"
    });


    /**
     * 返回类型
     */
    public static final String PARAM_RESULT_TYPE = "resultType";


    /**
     * 查询参数
     */
    public static final String PARAM_RECORD = "record";


}
