package com.mybatissist.mapper;

import com.mybatissist.constant.ProviderConstants;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

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
     * @param beanClass
     * @return
     */
    @DeleteProvider(type=com.mybatissist.provider.DeleteProvider.class,method="deleteByPrimaryKey")
    int deleteByPrimaryKey(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);

    /**
     * 根据record中所有非null属性删除记录
     * @param record
     * @param beanClass
     * @return
     */
    @DeleteProvider(type=com.mybatissist.provider.DeleteProvider.class,method="deleteBySelective")
    int deleteBySelective(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);


}
