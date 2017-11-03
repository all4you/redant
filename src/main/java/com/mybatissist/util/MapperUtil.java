package com.mybatissist.util;

import com.mybatissist.sqlsession.SqlSessionContext;

/**
 * Mapper工具类
 * @author gris.wang
 * @since 2017/11/3
 */
public class MapperUtil {


    /**
     * 获取Mapper
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getMapper(Class<T> clazz){
        return getMapper(true,clazz);
    }


    /**
     * 获取Mapper
     * @param autoCommit
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getMapper(boolean autoCommit,Class<T> clazz){
        return SqlSessionContext.getSqlSession(autoCommit).getMapper(clazz);
    }

}
