package com.redant.core.bean.context;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.ClassScaner;
import cn.hutool.core.util.StrUtil;
import com.redant.core.aware.BeanContextAware;
import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.init.InitFunc;
import com.redant.core.init.InitOrder;
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
 * DefaultBeanContext
 * @author houyi.wh
 * @date 2017-10-20
 */
@InitOrder(1)
public class DefaultBeanContext implements BeanContext, InitFunc {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBeanContext.class);

    /**
     * 保存所有的bean，初始化过一次后不会改变
     */
    private static Map<String,Object> beanMap;

    /**
     * bean加载完毕的标志
     */
    private static volatile boolean inited;

    /**
     * BeanContext的实例(单例)
     */
    private static DefaultBeanContext context;

    private DefaultBeanContext() {

    }

    public static BeanContext getInstance(){
        if(context==null) {
            synchronized (DefaultBeanContext.class) {
                if(context==null) {
                    context = new DefaultBeanContext();
                }
            }
        }
        return context;
    }

    @Override
    public void init() {
        // 初始化
        doInit();
    }

    /**
     * 根据bean的name获取具体的bean对象
     */
    @Override
    public Object getBean(String name) {
        return inited() ? beanMap.get(name) : null;
    }

    /**
     * 根据bean的name获取具体的bean对象
     */
    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        Object bean = getBean(name);
        return bean==null ? null : (T) bean;
    }

    /**
     * bean是否加载完毕
     */
    private boolean inited(){
        while(!inited){
            doInit();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return inited;
    }

    /**
     * 执行初始化工作
     */
    private void doInit() {
        // 初始化时需要同步
        synchronized (DefaultBeanContext.class){
            if(!inited){
                if(context==null) {
                    context = new DefaultBeanContext();
                }
                // 初始化bean
                initBean();
                // 对象注入
                injectAnnotation();
                // 注入BeanContext到BeanContextAware
                processBeanContextAware();
                inited = true;
                LOGGER.info("[DefaultBeanContext] doInit success!");
            }
        }
    }

    /**
     * 初始化Bean
     */
    private void initBean(){
        LOGGER.info("[DefaultBeanContext] Start initBean");
        try {
            /*
             * 扫描指定package下指定的类，并返回set
             */
            Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(CommonConstants.BEAN_SCAN_PACKAGE,Bean.class);
            beanMap = new LinkedHashMap<>(classSet.size()+1);
            if (CollectionUtil.isNotEmpty(classSet)) {
                /*
                 * 遍历所有类，找出有beanClass注解的类，并封装到linkedHashMap里
                 */
                for (Class<?> cls : classSet) {
                    Bean bean = cls.getAnnotation(Bean.class);
                    if (bean != null) {
                        String beanName = StrUtil.isNotBlank(bean.name())?bean.name():cls.getName();
                        if(beanMap.containsKey(beanName)){
                            LOGGER.warn("[DefaultBeanContext] Duplicate bean with name={}",beanName);
                            continue;
                        }
                        beanMap.put(beanName, cls.newInstance());
                    }
                }
                LOGGER.info("[DefaultBeanContext] initBean success!");
            }else{
                LOGGER.warn("[DefaultBeanContext] No bean classes scanned!");
            }
        } catch (Exception e) {
            LOGGER.error("[DefaultBeanContext] initBean error,cause:{}",e.getMessage(),e);
        }
    }


    /**
     * 注解处理器
     * 如果注解Autowired配置了name属性，则根据name所指定的名称获取要注入的实例引用，
     * 否则根据属性所属类型来扫描配置文件获取要注入的实例引用
     */
    private void injectAnnotation() {
        LOGGER.info("[DefaultBeanContext] Start injectAnnotation");
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object bean = entry.getValue();
            if (bean != null) {
                propertyAnnotation(bean);
                fieldAnnotation(bean);
            }
        }
        LOGGER.info("[DefaultBeanContext] injectAnnotation success!");
    }


    /**
     * 处理BeanContextAware
     */
    private void processBeanContextAware() {
        LOGGER.info("[DefaultBeanContext] Start processBeanContextAware");
        try {
            /*
             * 扫描指定package下指定的类，并返回set
             */
            Set<Class<?>> classSet = ClassScaner.scanPackageBySuper(CommonConstants.BEAN_SCAN_PACKAGE,BeanContextAware.class);
            if (CollectionUtil.isNotEmpty(classSet)) {
                for (Class<?> cls : classSet) {
                    // 如果cls是BeanContextAware的实现类
                    if(!cls.isInterface() && BeanContextAware.class.isAssignableFrom(cls)){
                        ((BeanContextAware)cls.newInstance()).setBeanContext(context);
                    }
                }
            }
            LOGGER.info("[DefaultBeanContext] ProcessBeanContextAware success!");
        } catch (Exception e) {
            LOGGER.error("[DefaultBeanContext] ProcessBeanContextAware error,cause:{}",e.getMessage(),e);
        }
    }

    /**
     * 处理在set方法加入的注解
     * @param bean 处理的bean
     */
    private void propertyAnnotation(Object bean){
        LOGGER.info("[DefaultBeanContext] Start propertyAnnotation");
        try {
            // 获取其属性的描述
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for(PropertyDescriptor descriptor : descriptors){
                // 获取所有set方法
                Method setter = descriptor.getWriteMethod();
                // 判断set方法是否定义了注解
                if(setter!=null && setter.isAnnotationPresent(Autowired.class)){
                    // 获取当前注解，并判断name属性是否为空
                    Autowired resource = setter.getAnnotation(Autowired.class);
                    String name;
                    Object value = null;
                    if(StrUtil.isNotBlank(resource.name())){
                        // 获取注解的name属性的内容
                        name = resource.name();
                        value = beanMap.get(name);
                    }else{ // 如果当前注解没有指定name属性,则根据类型进行匹配
                        for(Map.Entry<String,Object>  entry : beanMap.entrySet()){
                            // 判断当前属性所属的类型是否在beanHolderMap中存在
                            if(descriptor.getPropertyType().isAssignableFrom(entry.getValue().getClass())){
                                // 获取类型匹配的实例对象
                                value = entry.getValue();
                                break;
                            }
                        }
                    }
                    // 允许访问private方法
                    setter.setAccessible(true);
                    // 把引用对象注入属性
                    setter.invoke(bean, value);
                }
            }
            LOGGER.info("[DefaultBeanContext] propertyAnnotation success!");
        } catch (Exception e) {
            LOGGER.info("[DefaultBeanContext] propertyAnnotation error,cause:{}",e.getMessage(),e);
        }
    }

    /**
     * 处理在字段上的注解
     * @param bean 处理的bean
     */
    private void fieldAnnotation(Object bean){
        LOGGER.info("[DefaultBeanContext] Start fieldAnnotation");
        try {
            // 获取其全部的字段描述
            Field[] fields = bean.getClass().getDeclaredFields();
            for(Field field : fields){
                if(field!=null && field.isAnnotationPresent(Autowired.class)){
                    Autowired resource = field.getAnnotation(Autowired.class);
                    String name;
                    Object value = null;
                    if(StrUtil.isNotBlank(resource.name())){
                        name = resource.name();
                        value = beanMap.get(name);
                    }else{
                        for(Map.Entry<String,Object>  entry : beanMap.entrySet()){
                            // 判断当前属性所属的类型是否在配置文件中存在
                            if(field.getType().isAssignableFrom(entry.getValue().getClass())){
                                // 获取类型匹配的实例对象
                                value = entry.getValue();
                                break;
                            }
                        }
                    }
                    // 允许访问private字段
                    field.setAccessible(true);
                    // 把引用对象注入属性
                    field.set(bean, value);
                }
            }
            LOGGER.info("[DefaultBeanContext] fieldAnnotation success!");
        } catch (Exception e) {
            LOGGER.info("[DefaultBeanContext] fieldAnnotation error,cause:{}",e.getMessage(),e);
        }
    }


}