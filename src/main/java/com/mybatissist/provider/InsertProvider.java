package com.mybatissist.provider;


import com.mybatissist.constant.ProviderConstants;
import com.mybatissist.exception.InvalidProviderParamException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * 插入接口sql提供者
 * 当Mapper接口传递多个参数时，需要使用@Param注解标注，此时在SelectProvider对应的方法中需要通过Map接收参数
 * @author gris.wang
 * @create 2017-10-20
 */
public class InsertProvider {




    /**
     * 生成sql
     * @param tableName
     * @param columnProps
     * @param insertWithId
     * @return
     */
    private String createSql(String tableName,List<ColumnProp> columnProps,boolean insertWithId){
        if(CollectionUtils.isEmpty(columnProps)){
            throw new InvalidProviderParamException("Please provide the object to be inserted which should not be empty!");
        }
        String sql = new SQL(){{
            INSERT_INTO(tableName);
            for(ColumnProp columnProp : columnProps){
                if(insertWithId){
                    VALUES(columnProp.getColumn(),columnProp.getProp());
                }else{
                    if(!ProviderConstants.PRIMARY_KEY.equalsIgnoreCase(columnProp.getColumn())){
                        VALUES(columnProp.getColumn(),columnProp.getProp());
                    }
                }
            }
        }}.toString();
        return sql;
    }

    /**
     * 生成插入的values
     * @param columnProps
     * @param insertWithId
     * @return
     */
    private String createValues(String tableName,List<ColumnProp> columnProps,boolean insertWithId){
        String sql = createSql(tableName,columnProps,insertWithId);
        sql = sql.replaceAll("(INSERT[\\s\\S]*VALUES)",",");
        return sql;
    }

    //===============================================================

    /**
     * 插入单条记录
     * 使用自动生成的id
     * @param parameters
     * @return
     */
    public String insert(Map<String, Object> parameters){
        if(!ProviderHelper.parametersValid(parameters)){
            return null;
        }
        Class<?> beanClass = (Class)parameters.get(ProviderConstants.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderConstants.PARAM_RECORD)?parameters.get(ProviderConstants.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        List<ColumnProp> columnProps = ProviderHelper.getAllColumnProps(bean,ProviderConstants.PARAM_RECORD);

        String sql = createSql(tableName,columnProps,false);

        ProviderHelper.printSql(beanClass,bean,"insert",sql);
        return sql;
    }

    /**
     * 插入单条记录
     * 使用指定的id
     * @param parameters
     * @return
     */
    public String insertWithId(Map<String, Object> parameters){
        if(!ProviderHelper.parametersValid(parameters)){
            return null;
        }
        Class<?> beanClass = (Class)parameters.get(ProviderConstants.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderConstants.PARAM_RECORD)?parameters.get(ProviderConstants.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        List<ColumnProp> columnProps = ProviderHelper.getAllColumnProps(bean,ProviderConstants.PARAM_RECORD);

        String sql = createSql(tableName,columnProps,true);

        ProviderHelper.printSql(beanClass,bean,"insertWithId",sql);
        return sql;
    }


    /**
     * 插入单条记录,属性为null的字段不会插入
     * @param parameters
     * @return
     */
    public String insertSelective(Map<String, Object> parameters){
        if(!ProviderHelper.parametersValid(parameters)){
            return null;
        }
        Class<?> beanClass = (Class)parameters.get(ProviderConstants.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderConstants.PARAM_RECORD)?parameters.get(ProviderConstants.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        List<ColumnProp> columnProps = ProviderHelper.getColumnProps(bean,ProviderConstants.PARAM_RECORD);

        String sql = createSql(tableName,columnProps,false);

        ProviderHelper.printSql(beanClass,bean,"insertSelective",sql);
        return sql;
    }



}
