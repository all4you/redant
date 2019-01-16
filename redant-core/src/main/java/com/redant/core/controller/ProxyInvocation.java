package com.redant.core.controller;


import com.redant.core.TemporaryDataHolder;
import com.redant.core.common.exception.InvocationException;
import com.redant.core.common.exception.ValidationException;
import com.redant.core.common.util.GenericsUtil;
import com.redant.core.common.util.HttpRequestUtil;
import com.redant.core.converter.PrimitiveConverter;
import com.redant.core.converter.PrimitiveTypeUtil;
import com.redant.core.controller.annotation.Param;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 封装了ControllerProxy的调用过程
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ProxyInvocation {

	private static Invocation invocation = new Invocation();

	public static Object invoke(ControllerProxy proxy) throws Exception{
		Object controller = proxy.getController();
		Method method = proxy.getMethod();
		String methodName = proxy.getMethodName();
		return invocation.invoke(controller,method,methodName);
	}


	private static class Invocation {

		private static Logger logger = LoggerFactory.getLogger(Invocation.class);

		private final int KEY_VALUE_SIZE = 2;

		public Invocation(){

		}

		/**
		 * 获得方法调用的参数
		 * @param method 方法
		 * @param parameterTypes 方法参数类型
		 * @return 参数
		 * @throws Exception 参数异常
		 */
		private Object[] getParameters(Method method,Class<?>[] parameterTypes) throws Exception {
			//用于存放调用参数的对象数组
			Object[] parameters = new Object[parameterTypes.length];

			//获得所调用方法的参数的Annotation数组
			Annotation[][] annotationArray = method.getParameterAnnotations();

			//获取参数列表
			Map<String, List<String>> paramMap = HttpRequestUtil.getParameterMap(TemporaryDataHolder.loadHttpRequest());

			//构造调用所需要的参数数组
			for (int i = 0; i < parameterTypes.length; i++) {
				Object parameter;
				Class<?> type = parameterTypes[i];
				Annotation[] annotation = annotationArray[i];
				// 如果该参数没有RouterParam注解
				if (annotation == null || annotation.length == 0) {
					// 如果该参数类型是基础类型，则需要加RouterParam注解
					if(PrimitiveTypeUtil.isPriType(type)){
						logger.warn("Must specify a @Param annotation for primitive type parameter in method={}", method.getName());
						continue;
					}
					// 封装对象类型的parameter
					parameter = type.newInstance();
					BeanUtils.populate(parameter,paramMap);
					parameters[i] = parameter;
				}else{
					Param param = (Param) annotation[0];
					try{
						// 生成当前的调用参数v
						parameter = parseParameter(paramMap, type, param, method, i);
						if(param.notNull()){
							GenericsUtil.checkNull(param.key(), parameter);
						}
						if(param.notBlank()){
							GenericsUtil.checkBlank(param.key(), parameter);
						}
						parameters[i] = parameter;
					}catch(Exception e){
					    logger.error("param ["+param.key()+"] is invalid，cause:"+e.getMessage());
						throw new IllegalArgumentException("参数 "+param.key()+" 不合法："+e.getMessage());
					}
				}
			}
			return parameters;
		}


		/**
		 * GET 参数解析
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private Object parseParameter(Map<String, List<String>> paramMap, Class<?> type, Param param, Method method, int index) throws InstantiationException, IllegalAccessException{
			Object value = null;
			String key = param.key();
			String defaultValue= param.defaultValue();
			if(key.length() > 0){
				// 如果参数是map类型
				if(Map.class.isAssignableFrom(type)){
					if(index > 0){
						throw new ValidationException("Must have only one Map type parameter");
					}

					List<Class> types = GenericsUtil.getMethodGenericParameterTypes(method, index);
					if(types.size() == KEY_VALUE_SIZE && (types.get(0) != String.class || types.get(1) != String.class)){
						throw new ValidationException("Map type parameter must both be String, Occurring Point: " + method.toGenericString());
					}

					Map<String, String> valueMap = new HashMap<String, String>(paramMap.size());
					for(Map.Entry<String, List<String>> entry : paramMap.entrySet()){
						List<String> valueList = entry.getValue();
						valueMap.put(entry.getKey(), valueList.get(0));
					}
					value = valueMap;
				}else{
					List<String> params = paramMap.get(key);
					if(params != null){
						// 基础类型
						if(PrimitiveTypeUtil.isPriType(type)){
							value = PrimitiveConverter.getInstance().convert(params.get(0), type);

						// 数组
						}else if(type.isArray()){
							String[] strArray = params.toArray(new String[]{});
							value = PrimitiveConverter.getInstance().convert(strArray, type);

						// List
						}else if(List.class.isAssignableFrom(type)){
							List<Object> list;
							List<Class> types = GenericsUtil.getMethodGenericParameterTypes(method, index);
							Class<?> listType = types.size() == 1?types.get(0):String.class;
							if(List.class == type){
								list = new ArrayList<Object>();
							}else{
								list = (List<Object>) type.newInstance();
							}
							for(int i = 0; i < params.size(); i++){
								if(params.get(i).length() > 0){
									list.add(PrimitiveConverter.getInstance().convert(params.get(i), listType));
								}
							}
							value = list;
						}
					}else{
						if(PrimitiveTypeUtil.isPriType(type)){
							value = PrimitiveConverter.getInstance().convert(defaultValue, type);
						}
					}
				}
			}
			return value;
		}


		/**
		 * 返回调用异常
		 * @param msg 消息
		 * @param cause 异常
		 * @return 调用异常
		 */
		private InvocationException getInvokeException(String msg, Throwable cause){
			return new InvocationException(msg,cause);
		}


		//==================================


		/**
		 * 执行方法的调用
		 * @param controller 控制器
		 * @param method 方法
		 * @param methodName 方法名
		 * @return 渲染结果
		 * @throws Exception 异常
		 */
        Object invoke(Object controller, Method method, String methodName) throws Exception {
			if (method == null) {
				throw new NoSuchMethodException("Can not find specified method: " + methodName);
			}

			Class<?> clazz = controller.getClass();
			Class<?>[] parameterTypes = method.getParameterTypes();
			Object[] parameters = null;
			Object result;
			try {
				parameters = getParameters(method,parameterTypes);
				// 使用 CGLib 执行反射调用
				FastClass fastClass = FastClass.create(clazz);
				FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
				// 调用，并得到调用结果
				result = fastMethod.invoke(controller, parameters);

			} catch(InvocationTargetException e){
				String msg = "调用出错,请求类["+controller.getClass().getName()+"],方法名[" + method.getName() + "],参数[" + Arrays.toString(parameters)+"]";
				throw getInvokeException(msg, e);
			}
			return result;
		}

	}



}
