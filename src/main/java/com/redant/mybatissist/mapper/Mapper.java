package com.redant.mybatissist.mapper;

/**
 * 基础Mapper，其他Mapper都继承自该接口
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface Mapper<T> extends InsertMapper<T>,UpdateMapper<T>,DeleteMapper<T>,SelectMapper<T> {

}
