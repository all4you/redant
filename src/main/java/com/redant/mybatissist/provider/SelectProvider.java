package com.redant.mybatissist.provider;


import com.redant.mybatissist.enums.QueryModel;
import com.redant.mybatissist.enums.QueryStyle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 查询接口sql提供者
 * 当Mapper接口传递多个参数时，需要使用@Param注解标注，此时在SelectProvider对应的方法中需要通过Map接收参数
 * @author gris.wang
 * @create 2017-10-20
 */
public class SelectProvider {


    /**
     * 查询记录数
     * @param parameters
     * @return
     */
    public String selectCount(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_BEAN_CLASS);
        Object bean = parameters.get(ProviderHelper.PARAM_RECORD);
        String tableName = ProviderHelper.getTableName(beanClass);
        String alias = ProviderHelper.getTableAlias(beanClass);
        String pk = ProviderHelper.getPrimaryKey(beanClass);
        List<QueryCondition> queryConditions = ProviderHelper.getQueryConditions(bean,ProviderHelper.PARAM_RECORD);

        String sql = new SQL(){{
            SELECT("COUNT("+alias+ProviderHelper.DOT+pk+")");
            FROM(tableName+alias);
            if(CollectionUtils.isNotEmpty(queryConditions)){
                for(QueryCondition condition : queryConditions){
                    if(condition.getQueryStyle()==QueryStyle.OR){
                        OR();
                    }
                    String queryModel = condition.getQueryModel()==QueryModel.EQUAL?" = ":" LIKE ";
                    WHERE(alias+ProviderHelper.DOT+condition.getColumn()+queryModel+condition.getProp());
                }
            }
        }}.toString();

        ProviderHelper.printSql(beanClass,bean,"selectCount",sql);
        return sql;
    }

    /**
     * 查询单条记录
     * @param parameters
     * @return
     */
    public String selectOne(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_BEAN_CLASS);
        Object bean = parameters.get(ProviderHelper.PARAM_RECORD);
        String tableName = ProviderHelper.getTableName(beanClass);
        String alias = ProviderHelper.getTableAlias(beanClass);
        // 获取所有要查询的列
        List<String> columns = ProviderHelper.getColumns(beanClass);
        StringBuilder sb = new StringBuilder();
        for(int i=0,s=columns.size();i<s;i++){
            sb.append(alias+ProviderHelper.DOT+columns.get(i));
            if(i<s-1){
                sb.append(",");
            }
        }
        List<QueryCondition> queryConditions = ProviderHelper.getQueryConditions(bean,ProviderHelper.PARAM_RECORD);

        String sql = new SQL(){{
            SELECT(sb.toString());
            FROM(tableName+alias);
            if(CollectionUtils.isNotEmpty(queryConditions)) {
                for (QueryCondition condition : queryConditions) {
                    if (condition.getQueryStyle() == QueryStyle.OR) {
                        OR();
                    }
                    String queryModel = condition.getQueryModel() == QueryModel.EQUAL ? " = " : " LIKE ";
                    WHERE(alias+ProviderHelper.DOT+condition.getColumn()+queryModel+condition.getProp());
                }
            }
        }}.toString();

        ProviderHelper.printSql(beanClass,bean,"selectOne",sql);
        return sql;
    }

}
