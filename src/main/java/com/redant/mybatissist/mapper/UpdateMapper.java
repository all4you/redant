package com.redant.mybatissist.mapper;

/**
 * 更新接口
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface UpdateMapper<T> {

    /**
     * 根据主键更新记录
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);

    /**
     * 根据主键更新记录,属性为null的字段不会更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 根据指定的key更新记录
     * @param record
     * @return
     */
    int updateByKey(T record,Object key);

    /**
     * 根据指定的key更新记录,属性为null的字段不会更新
     * @param record
     * @return
     */
    int updateByKeySelective(T record,Object key);


}
