package com.mybatissist.mapper;

import java.util.List;

/**
 * 插入接口
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface InsertMapper<T> {

    /**
     * 插入单条记录
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 插入单条记录,属性为null的字段不会插入
     * @param record
     * @return
     */
    int insertSelective(T record);

    /**
     * 批量插入记录
     * @param records
     * @return
     */
    int insertBatch(List<T> records);

    /**
     * 批量插入记录,属性为null的字段不会插入
     * @param records
     * @return
     */
    int insertBatchSelective(List<T> records);


}
