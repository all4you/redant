package com.mybatissist.mapper;

import com.mybatissist.constant.ProviderConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

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
     * @param beanClass
     * @return
     */
    @UpdateProvider(type=com.mybatissist.provider.UpdateProvider.class,method="updateByPrimaryKey")
    int updateByPrimaryKey(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);

    /**
     * 根据主键更新记录,属性为null的字段不会更新
     * @param record
     * @param beanClass
     * @return
     */
    @UpdateProvider(type=com.mybatissist.provider.UpdateProvider.class,method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass);

    /**
     * 根据指定的key更新记录
     * @param record
     * @param beanClass
     * @param keys
     * @return
     */
    @UpdateProvider(type=com.mybatissist.provider.UpdateProvider.class,method="updateByKey")
    int updateByKey(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass, @Param(ProviderConstants.PARAM_KEYS) List<String> keys);

    /**
     * 根据指定的key更新记录,属性为null的字段不会更新
     * @param record
     * @param beanClass
     * @param keys
     * @return
     */
    @UpdateProvider(type=com.mybatissist.provider.UpdateProvider.class,method="updateByKeySelective")
    int updateByKeySelective(@Param(ProviderConstants.PARAM_RECORD) T record, @Param(ProviderConstants.PARAM_BEAN_CLASS) Class<T> beanClass, @Param(ProviderConstants.PARAM_KEYS) List<String> keys);


}
