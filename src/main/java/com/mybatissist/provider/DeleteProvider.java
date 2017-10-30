package com.mybatissist.provider;

import com.mybatissist.constant.ProviderConstants;
import com.mybatissist.enums.QueryModel;
import com.mybatissist.enums.QueryStyle;
import com.mybatissist.exception.InvalidProviderParamException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 删除接口sql提供者
 * @auther gris.wang
 * @since  2017/10/30
 */
public class DeleteProvider {


    /**
     * 生成sql
     * @param parameters
     * @param deleteByPK
     * @param methodName
     * @return
     */
    private String createSql(Map<String, Object> parameters,boolean deleteByPK,String methodName){
        if(!ProviderHelper.parametersValid(parameters)){
            return null;
        }
        Class<?> beanClass = (Class)parameters.get(ProviderConstants.PARAM_BEAN_CLASS);
        Object bean = parameters.containsKey(ProviderConstants.PARAM_RECORD)?parameters.get(ProviderConstants.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        List<ColumnProp> conditions;
        if(deleteByPK){
            String pk = ProviderHelper.getPrimaryKey(beanClass);
            List<String> keys =  Arrays.asList(new String[]{pk});
            conditions = ProviderHelper.getConditions(bean,ProviderConstants.PARAM_RECORD,keys);
        }else{
            conditions = ProviderHelper.getColumnProps(bean,ProviderConstants.PARAM_RECORD);
        }
        if(CollectionUtils.isEmpty(conditions)){
            throw new InvalidProviderParamException("Please provide the object to be deleted which should not be empty!");
        }
        String sql = new SQL(){{
            DELETE_FROM(tableName);
            if(CollectionUtils.isNotEmpty(conditions)){
                for(ColumnProp condition : conditions){
                    if(condition.getQueryStyle() == QueryStyle.OR){
                        OR();
                    }
                    String queryModel = condition.getQueryModel() == QueryModel.EQUAL ? " = " : " LIKE ";
                    WHERE(condition.getColumn()+queryModel+condition.getProp());
                }
            }
        }}.toString();

        ProviderHelper.printSql(beanClass,bean,methodName,sql);
        return sql;
    }

    //=================================================


    /**
     * 根据主键删除记录
     * @param parameters
     * @return
     */
    public String deleteByPrimaryKey(Map<String, Object> parameters){
        return createSql(parameters,true,"deleteByPrimaryKey");
    }

    /**
     * 根据record中所有非null属性删除记录
     * @param parameters
     * @return
     */
    public String deleteBySelective(Map<String, Object> parameters){
        return createSql(parameters,false,"deleteBySelective");
    }

}
