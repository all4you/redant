package com.mybatissist.provider;


import com.mybatissist.annotation.Column;
import com.mybatissist.annotation.Id;
import com.mybatissist.annotation.Table;
import com.mybatissist.constant.ProviderConstants;
import com.mybatissist.enums.NameStyle;
import com.mybatissist.enums.QueryModel;
import com.mybatissist.enums.QueryStyle;
import com.mybatissist.exception.InvalidProviderParamException;
import com.mybatissist.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基础方法提供者
 * @author gris.wang
 * @create 2017-10-20
 */
public class ProviderHelper {


    /**
     * 获取日志对象
     * @return
     */
    public static Logger logger = LoggerFactory.getLogger(ProviderHelper.class);


    /**
     * 从field中转换出ColumnProp
     * @param field
     * @param prefix
     * @return
     */
    private static ColumnProp parseColumnProp(Field field, String prefix){
        QueryStyle queryStyle = QueryStyle.AND;
        QueryModel queryModel = QueryModel.EQUAL;
        String column;
        String prop = ProviderConstants.PROP_LEFT + (StringUtils.isNotBlank(prefix)?prefix+ ProviderConstants.DOT:"") + field.getName() + ProviderConstants.PROP_RIGHT;
        // 如果当前字段有Column注解
        if(field.isAnnotationPresent(Column.class)){
            Column c = field.getAnnotation(Column.class);
            // 如果该字段设置了忽略
            if(c.ignore()){
                return null;
            }
            queryStyle = c.queryStyle();
            queryModel = c.queryModel();
            column = StringUtil.isNotBlank(c.name())?c.name():StringUtil.convertByNameStyle(field.getName(),NameStyle.CAMEL_HUMP);
        }else{
            column = StringUtil.convertByNameStyle(field.getName(),NameStyle.CAMEL_HUMP);
        }
        return new ColumnProp(queryStyle,queryModel,column,prop);
    }


    //===============================================================

    /**
     * 检查参数
     * @param parameters
     * @return
     */
    public static boolean parametersValid(Map<String, Object> parameters){
        if(!parameters.containsKey(ProviderConstants.PARAM_BEAN_CLASS) || null==parameters.get(ProviderConstants.PARAM_BEAN_CLASS)){
            throw new InvalidProviderParamException("Must provide @Param(\""+ProviderConstants.PARAM_BEAN_CLASS +"\")");
        }
        return true;
    }

    /**
     * 获取表名
     * @param beanClass
     * @return
     */
    public static String getTableName(Class<?> beanClass){
        String tableName;
        if(beanClass.isAnnotationPresent(Table.class)){
            Table bean = beanClass.getAnnotation(Table.class);
            NameStyle style = bean.style();
            tableName = StringUtil.isNotBlank(bean.name())?bean.name():StringUtil.convertByNameStyle(beanClass.getSimpleName(),style);
        }else{
            tableName = StringUtil.convertByNameStyle(beanClass.getSimpleName(),NameStyle.CAMEL_HUMP);
        }
        return ProviderConstants.SPACE+tableName+ProviderConstants.SPACE;
    }

    /**
     * 获取表的别名
     * @param beanClass
     * @return
     */
    public static String getTableAlias(Class<?> beanClass){
        String tableAlias;
        if(beanClass.isAnnotationPresent(Table.class)){
            Table bean = beanClass.getAnnotation(Table.class);
            tableAlias = StringUtil.isNotBlank(bean.alias())?bean.alias():StringUtil.convertByNameStyle(beanClass.getSimpleName(),NameStyle.LOWER_CASE);
        }else{
            tableAlias = StringUtil.convertByNameStyle(beanClass.getSimpleName(),NameStyle.LOWER_CASE);
        }
        return tableAlias;
    }

    /**
     * 获取所有字段
     * @param beanClass
     * @return
     */
    public static List<Field> getFields(Class<?> beanClass) {
        List<Field> fields = new ArrayList<Field>();
        Field[] beanFields = (Field[])ArrayUtils.addAll(beanClass.getDeclaredFields(),beanClass.getSuperclass().getDeclaredFields());
        for(int i=0,s=beanFields.length;i<s;i++){
            Field field = beanFields[i];
            if (!ProviderConstants.ABANDON_FIELDS.contains(field.getName().toUpperCase())) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * 获取所有列名
     * @param beanClass
     * @return
     */
    public static List<String> getColumns(Class<?> beanClass) {
        List<String> columns = new ArrayList<String>();
        List<Field> fields = getFields(beanClass);
        for(Field field : fields){
            String column;
            // 如果当前字段有Column注解
            if(field.isAnnotationPresent(Column.class)){
                Column c = field.getAnnotation(Column.class);
                // 如果该字段设置了忽略
                if(c.ignore()){
                    continue;
                }
                column = StringUtil.isNotBlank(c.name())?c.name():StringUtil.convertByNameStyle(field.getName(),NameStyle.CAMEL_HUMP);
            }else{
                column = StringUtil.convertByNameStyle(field.getName(),NameStyle.CAMEL_HUMP);
            }
            // 此处要处理为 select user_name as userName,否则结果转换为bean时无法找到userName属性
            if(!column.equals(field.getName())){
                column = column+ProviderConstants.AS+field.getName();
            }
            columns.add(column);
        }
        return columns;
    }

    /**
     * 获取主键Id
     * @param beanClass
     * @return
     */
    public static String getPrimaryKey(Class<?> beanClass){
        String id = "";
        List<Field> beanFields = getFields(beanClass);
        for (Field field : beanFields) {
            // 如果当前字段有Id注解
            if(field.isAnnotationPresent(Id.class)){
                Id i = field.getAnnotation(Id.class);
                id = StringUtil.isNotBlank(i.name())?i.name():field.getName();
                break;
            }
        }
        if(StringUtil.isBlank(id)){
            logger.warn("No primaryKey column found in beanClass:[{}],will use 'id' instead.",beanClass);
            id = "id";
        }
        return id;
    }


    /**
     * 获取非空查询条件
     * @param bean
     * @param prefix
     * @return
     */
    public static List<ColumnProp> getColumnProps(Object bean, String prefix){
        List<ColumnProp> columnProps = new ArrayList<ColumnProp>();
        if(bean==null){
            return columnProps;
        }
        try{
            List<Field> beanFields = getFields(bean.getClass());
            for (Field field : beanFields) {
                field.setAccessible(true);
                Object val = field.get(bean);
                if(val==null){
                    continue;
                }
                ColumnProp columnProp = parseColumnProp(field,prefix);
                if(columnProp ==null){
                    continue;
                }
                columnProps.add(columnProp);
            }
        }catch (Exception e){
            logger.error("getColumnProps error,cause:",e);
        }
        return columnProps;
    }


    /**
     * 获取所有查询条件，包括空值
     * @param bean
     * @param prefix
     * @return
     */
    public static List<ColumnProp> getAllColumnProps(Object bean, String prefix){
        List<ColumnProp> columnProps = new ArrayList<ColumnProp>();
        if(bean==null){
            return columnProps;
        }
        try{
            List<Field> beanFields = getFields(bean.getClass());
            for (Field field : beanFields) {
                field.setAccessible(true);
                ColumnProp columnProp = parseColumnProp(field,prefix);
                if(columnProp ==null){
                    continue;
                }
                columnProps.add(columnProp);
            }
        }catch (Exception e){
            logger.error("getAllColumnProps error,cause:",e);
        }
        return columnProps;
    }

    /**
     * 根据指定的keys获取条件
     * @param bean
     * @param prefix
     * @return
     */
    public static List<ColumnProp> getConditions(Object bean, String prefix,List<String> keys){
        List<ColumnProp> columnProps = new ArrayList<ColumnProp>();
        if(bean==null || CollectionUtils.isEmpty(keys)){
            return columnProps;
        }
        try{
            List<Field> beanFields = getFields(bean.getClass());
            for (Field field : beanFields) {
                field.setAccessible(true);
                if(!keys.contains(field.getName())){
                    continue;
                }
                Object val = field.get(bean);
                if(val==null){
                    continue;
                }
                ColumnProp columnProp = parseColumnProp(field,prefix);
                if(columnProp ==null){
                    continue;
                }
                columnProps.add(columnProp);
            }
        }catch (Exception e){
            logger.error("getColumnProps error,cause:",e);
        }
        return columnProps;
    }


    public static void printSql(Class<?> beanClass,Object bean,String methodName,String sql){
        String param;
        if(bean instanceof List){
            List list = (List)bean;
            param = CollectionUtils.isNotEmpty(list)?ArrayUtils.toString(list):"";
        }else{
            param = bean!=null?bean.toString():"";
        }
        logger.info("\n==========================sql info=========================="+
                    "\nbeanClass:{},methodName:{}"+
                    "\nexecute sql:\n{}\nparams:{}"+
                    "\n==========================sql info==========================",beanClass,methodName,sql,param);
    }

    /**
     * 用以测试
     */
    private static class User implements Serializable{
        private static final long serialVersionUID = -4976516540408695147L;
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        List<Field> beanFields = ProviderHelper.getFields(User.class);
        logger.info(ArrayUtils.toString(beanFields));
    }

}
