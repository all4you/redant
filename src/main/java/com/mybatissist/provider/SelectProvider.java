package com.mybatissist.provider;


import com.mybatissist.enums.QueryModel;
import com.mybatissist.enums.QueryStyle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

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
     * 构造查询列
     * @param beanClass
     * @param alias
     * @return
     */
    private String makeQueryColumns(Class<?> beanClass,String alias){
        // 获取所有要查询的列
        List<String> columnList = ProviderHelper.getColumns(beanClass);
        StringBuilder columns = new StringBuilder();
        for(int i=0,s=columnList.size();i<s;i++){
            columns.append(alias+ProviderHelper.DOT+columnList.get(i));
            if(i<s-1){
                columns.append(",");
            }
        }
        return columns.toString();
    }

    /**
     *
     * @param parameters
     * @return
     */
    private String createSql(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderHelper.PARAM_RECORD)?parameters.get(ProviderHelper.PARAM_RECORD):null;
        String tableName = ProviderHelper.getTableName(beanClass);
        String alias = ProviderHelper.getTableAlias(beanClass);
        String columns = makeQueryColumns(beanClass,alias);
        List<QueryCondition> queryConditions = ProviderHelper.getQueryConditions(bean,ProviderHelper.PARAM_RECORD);

        String sql = new SQL(){{
            SELECT(columns);
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
        return sql;
    }


    //===============================================================

    /**
     * 查询记录数
     * @param parameters
     * @return
     */
    public String selectCount(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_RESULT_TYPE);
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
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderHelper.PARAM_RECORD)?parameters.get(ProviderHelper.PARAM_RECORD):null;

        String sql = createSql(parameters);
        sql += "\nLIMIT 1";

        ProviderHelper.printSql(beanClass,bean,"selectOne",sql);
        return sql;
    }

    /**
     * 查询多条记录
     * @param parameters
     * @return
     */
    public String selectList(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderHelper.PARAM_RECORD)?parameters.get(ProviderHelper.PARAM_RECORD):null;

        String sql = createSql(parameters);

        ProviderHelper.printSql(beanClass,bean,"selectList",sql);
        return sql;
    }

    /**
     * 查询所有记录
     * @param parameters
     * @return
     */
    public String selectAll(Map<String, Object> parameters){
        Class<?> beanClass = (Class)parameters.get(ProviderHelper.PARAM_RESULT_TYPE);
        Object bean = parameters.containsKey(ProviderHelper.PARAM_RECORD)?parameters.get(ProviderHelper.PARAM_RECORD):null;

        String sql = createSql(parameters);

        ProviderHelper.printSql(beanClass,bean,"selectAll",sql);
        return sql;
    }

}
