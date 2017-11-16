package com.mybatissist.mapper;

import com.mybatissist.constant.ProviderConstants;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * 插入接口
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface InsertMapper<T> {

    /**
     * 插入单条记录
     * 实体必须包含`id`属性，并且必须为自增列
     * @param record
     * @param beanClass
     * @return
     */
    @Options(useGeneratedKeys=true,keyProperty=ProviderConstants.PARAM_RECORD+".id")
    @InsertProvider(type=com.mybatissist.provider.InsertProvider.class,method="insert")
    int insert(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);

    /**
     * 插入单条记录
     * 使用指定的id
     * @param record
     * @param beanClass
     * @return
     */
    @InsertProvider(type=com.mybatissist.provider.InsertProvider.class,method="insertWithId")
    int insertWithId(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);


    /**
     * 插入单条记录,属性为null的字段不会插入
     * @param record
     * @param beanClass
     * @return
     */
    @Options(useGeneratedKeys=true,keyProperty=ProviderConstants.PARAM_RECORD+".id")
    @InsertProvider(type=com.mybatissist.provider.InsertProvider.class,method="insertSelective")
    int insertSelective(@Param(ProviderConstants.PARAM_RECORD) T record,@Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);



}
