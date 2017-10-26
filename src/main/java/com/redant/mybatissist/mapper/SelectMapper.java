package com.redant.mybatissist.mapper;

import com.redant.mybatissist.provider.ProviderHelper;
import com.redant.mybatissist.provider.SqlProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 查询接口
 * 当Mapper接口传递多个参数时，需要使用@Param注解标注，此时在SelectProvider对应的方法中需要通过Map接收参数
 * @param <T>
 * @author gris.wang
 * @create 2017-10-20
 */
public interface SelectMapper<T> {

    /**
     * 查询记录数
     * @param beanClass
     * @param record
     * @return
     */
    @SelectProvider(type=SqlProvider.class,method="selectCount")
    int selectCount(@Param(ProviderHelper.PARAM_BEAN_CLASS) Class<?> beanClass,@Param(ProviderHelper.PARAM_RECORD) T record);

    /**
     * 查询单条记录
     * @param beanClass
     * @param record
     * @return
     */
    @SelectProvider(type=SqlProvider.class,method="selectOne")
    T selectOne(@Param(ProviderHelper.PARAM_BEAN_CLASS) Class<?> beanClass,@Param(ProviderHelper.PARAM_RECORD) T record);

    /**
     * 查询列表
     * @param beanClass
     * @param record
     * @return
     */
    List<T> selectList(@Param(ProviderHelper.PARAM_BEAN_CLASS) Class<?> beanClass,@Param(ProviderHelper.PARAM_RECORD) T record);

    /**
     * 查询所有记录
     * @param beanClass
     * @return
     */
    List<T> selectAll(@Param(ProviderHelper.PARAM_BEAN_CLASS) Class<?> beanClass);

}
