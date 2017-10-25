package com.redant.core.bean;


import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.common.constants.CommonConstants;
import com.redant.common.util.ThreadUtil;
import com.xiaoleilu.hutool.lang.ClassScaner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * BeanHolder
 * @author gris.wang
 * @create 2017-10-20
 */
public class BeanHolder {

    private static final Logger logger = LoggerFactory.getLogger(BeanHolder.class);

    /**
     * 保存所有的bean，初始化过一次后不会改变
     */
    private Map<String,Object> beanHolderMap;

    /**
     * bean加载完毕的标志
     */
    private volatile boolean beanLoaded;

    /**
     * BeanHolder的实例(单例)
     */
    private static BeanHolder holder;

    private BeanHolder(){

    }


    /**
     * 处理在set方法加入的注解
     * @param bean 处理的bean
     */
    private void propertyAnnotation(Object bean){
        try {
            //获取其属性的描述
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for(PropertyDescriptor descriptor : descriptors){
                //获取所有set方法
                Method setter = descriptor.getWriteMethod();
                //判断set方法是否定义了注解
                if(setter!=null && setter.isAnnotationPresent(Autowired.class)){
                    //获取当前注解，并判断name属性是否为空
                    Autowired resource = setter.getAnnotation(Autowired.class);
                    String name;
                    Object value = null;
                    if(StringUtils.isNotBlank(resource.name())){
                        //获取注解的name属性的内容
                        name = resource.name();
                        value = beanHolderMap.get(name);
                    }else{ //如果当前注解没有指定name属性,则根据类型进行匹配
                        for(Map.Entry<String,Object>  entry : beanHolderMap.entrySet()){
                            //判断当前属性所属的类型是否在beanHolderMap中存在
                            if(descriptor.getPropertyType().isAssignableFrom(entry.getValue().getClass())){
                                //获取类型匹配的实例对象
                                value = entry.getValue();
                                break;
                            }
                        }
                    }
                    //允许访问private方法
                    setter.setAccessible(true);
                    //把引用对象注入属性
                    setter.invoke(bean, value);
                }
            }
        } catch (Exception e) {
            logger.info("propertyAnnotation error,cause:",e);
        }
    }



    /**
     * 处理在字段上的注解
     * @param bean 处理的bean
     */
    private void fieldAnnotation(Object bean){
        try {
            //获取其全部的字段描述
            Field[] fields = bean.getClass().getDeclaredFields();
            for(Field field : fields){
                if(field!=null && field.isAnnotationPresent(Autowired.class)){
                    Autowired resource = field.getAnnotation(Autowired.class);
                    String name;
                    Object value = null;
                    if(StringUtils.isNotBlank(resource.name())){
                        name = resource.name();
                        value = beanHolderMap.get(name);
                    }else{
                        for(Map.Entry<String,Object>  entry : beanHolderMap.entrySet()){
                            //判断当前属性所属的类型是否在配置文件中存在
                            if(field.getType().isAssignableFrom(entry.getValue().getClass())){
                                //获取类型匹配的实例对象
                                value = entry.getValue();
                                break;
                            }
                        }
                    }
                    //允许访问private字段
                    field.setAccessible(true);
                    //把引用对象注入属性
                    field.set(bean, value);
                }
            }
        } catch (Exception e) {
            logger.info("fieldAnnotation error,cause:",e);
        }
    }


    /**
     * bean是否加载完毕
     * @return
     */
    private boolean beanLoaded(){
        while(!beanLoaded){
            initBeans();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return beanLoaded;
    }



    //==================================================================

    /**
     * 创建BeanHolder实例
     * @return
     */
    public static BeanHolder create(){
        synchronized (BeanHolder.class){
            if(holder==null){
                holder = new BeanHolder();
            }
            return holder;
        }
    }


    /**
     * 初始化bean
     */
    public void initBeans(){
        // 初始化beanHolderMap时需要同步
        synchronized (BeanHolder.class){
            if(null==beanHolderMap) {
                logger.info("Start init bean classes...currentThread={}", ThreadUtil.currentThreadName());
                try {
                    /**
                     * 扫描指定package下指定的类，并返回set
                     */
                    Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(CommonConstants.BEAN_SCAN_PACKAGE,Bean.class);
                    beanHolderMap = new LinkedHashMap<String,Object>(classSet.size());
                    if (CollectionUtils.isNotEmpty(classSet)) {
                        /**
                         * 遍历所有类，找出有beanClass注解的类，并封装到linkedHashMap里
                         */
                        for (Class<?> cls : classSet) {
                            Bean bean = cls.getAnnotation(Bean.class);
                            if (bean != null) {
                                String beanName = StringUtils.isNotBlank(bean.name())?bean.name():cls.getName();
                                if(beanHolderMap.containsKey(beanName)){
                                    logger.warn("Duplicate bean with name={}",beanName);
                                    continue;
                                }
                                beanHolderMap.put(beanName, cls.newInstance());
                            }
                        }
                        logger.info("Init bean classes success!");
                    }else{
                        logger.warn("No bean classes scanned!");
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("Init bean classes error,cause:",e);
                }
            }
            beanLoaded = true;
        }
    }


    /**
     * 注解处理器
     * 如果注解Autowired配置了name属性，则根据name所指定的名称获取要注入的实例引用，
     * 如果注解Autowired;没有配置name属性，则根据属性所属类型来扫描配置文件获取要
     * 注入的实例引用
     *
     */
    public void annotationInject(){
        if(beanLoaded()){
            logger.info("Start annotationInject...");
            for(Map.Entry<String,Object>  entry : beanHolderMap.entrySet()){
                Object bean = entry.getValue();
                if(bean!=null){
                    propertyAnnotation(bean);
                    fieldAnnotation(bean);
                }
            }
            logger.info("annotationInject success!");
        }
    }

    /**
     * 根据bean的name获取具体的bean对象
     * @param name
     * @return
     */
    public Object getBean(String name) {
        if(beanLoaded()){
            return beanHolderMap.get(name);
        }
        return null;
    }

    /**
     * 根据bean的name获取具体的bean对象
     * @param name
     * @param clazz
     * @return
     */
    public <T>T getBean(String name,Class<T> clazz) {
        Object bean = getBean(name);
        return bean==null?null:(T)bean;
    }


}