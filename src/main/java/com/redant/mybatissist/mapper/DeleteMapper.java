package com.redant.mybatissist.mapper;

/**
 * 删除接口
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface DeleteMapper<T> {

    /**
     * 根据主键删除记录
     * @param record
     * @return
     */
    int deleteByPrimaryKey(T record);

    /**
     * 根据record中所有非null属性删除记录
     * @param record
     * @return
     */
    int deleteBySelective(T record);


}
