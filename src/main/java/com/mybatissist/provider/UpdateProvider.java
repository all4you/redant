package com.mybatissist.provider;

import com.mybatissist.constant.ProviderConstants;
import com.mybatissist.enums.QueryModel;
import com.mybatissist.enums.QueryStyle;
import com.mybatissist.exception.InvalidProviderParamException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 更新接口sql提供者
 * @auther gris.wang
 * @since  2017/10/30
 */
public class UpdateProvider {


    /**
     * 生成sql
     * @param parameters
     * @param updateSelective
     * @param updateByPK
     * @param methodName
     * @return
     */
    private String createSql(Map<String, Object> parameters,boolean updateSelective,boolean updateByPK,String methodName){
        if(!ProviderHelper.parametersValid(parameters)){
            return null;
        }
        Class<?> beanClass = (Class)parameters.get(ProviderConstants.PARAM_BEAN_CLASS);
        Object bean = parameters.containsKey(ProviderConstants.PARAM_RECORD)?parameters.get(ProviderConstants.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        List<String> keys;
        if(updateByPK){
            String pk = ProviderHelper.getPrimaryKey(beanClass);
            keys =  Arrays.asList(new String[]{pk});
        }else{
            keys = parameters.containsKey(ProviderConstants.PARAM_KEYS)?(List<String>)parameters.get(ProviderConstants.PARAM_KEYS): Collections.emptyList();
        }

        List<ColumnProp> columnProps = updateSelective?ProviderHelper.getColumnProps(bean,ProviderConstants.PARAM_RECORD):ProviderHelper.getAllColumnProps(bean,ProviderConstants.PARAM_RECORD);
        List<ColumnProp> conditions = ProviderHelper.getConditions(bean,ProviderConstants.PARAM_RECORD,keys);

        if(CollectionUtils.isEmpty(columnProps)){
            throw new InvalidProviderParamException("Please provide the object to be updated which should not be empty!");
        }
        String sql = new SQL(){{
            UPDATE(tableName);
            for(ColumnProp columnProp : columnProps){
                SET(columnProp.getColumn()+"="+columnProp.getProp());
            }
            WHERE("1=1");
            for(ColumnProp condition : conditions){
                if(condition.getQueryStyle() == QueryStyle.OR){
                    OR();
                }
                String queryModel = condition.getQueryModel() == QueryModel.EQUAL ? " = " : " LIKE ";
                WHERE(condition.getColumn()+queryModel+condition.getProp());
            }
        }}.toString();

        ProviderHelper.printSql(beanClass,bean,methodName,sql);
        return sql;
    }

    //=================================================

    /**
     * 根据主键更新记录
     * @param parameters
     * @return
     */
    public String updateByPrimaryKey(Map<String, Object> parameters){
        return createSql(parameters,false,true,"updateByPrimaryKey");
    }

    /**
     * 根据主键更新记录,属性为null的字段不会更新
     * @param parameters
     * @return
     */
    public String updateByPrimaryKeySelective(Map<String, Object> parameters){
        return createSql(parameters,true,true,"updateByPrimaryKeySelective");
    }

    /**
     * 根据指定的key更新记录
     * @param parameters
     * @return
     */
    public String updateByKey(Map<String, Object> parameters){
        return createSql(parameters,false,false,"updateByKey");
    }

    /**
     * 根据指定的key更新记录,属性为null的字段不会更新
     * @param parameters
     * @return
     */
    public String updateByKeySelective(Map<String, Object> parameters){
        return createSql(parameters,true,false,"updateByKeySelective");
    }
    
}
