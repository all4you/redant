package com.redant.core.common.util;

import com.redant.core.common.exception.ValidationException;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * GenericsUtil
 * @author gris.wang
 * @date 2017-10-20
 */
public class GenericsUtil {
	
	/**
	 * 通过反射获得Class声明的范型Class.
	 * 通过反射,获得方法输入参数第index个输入参数的所有泛型参数的实际类型. 如: public void add(Map<String, Buyer> maps, List<String> names){}
	 * @param method 方法
	 * @param index 第几个输入参数
	 * @return 输入参数的泛型参数的实际类型集合, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回空集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class> getMethodGenericParameterTypes(Method method, int index) {
		List<Class> results = new ArrayList<Class>();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		if (index >= genericParameterTypes.length || index < 0) {
			throw new RuntimeException("你输入的索引" + (index < 0 ? "不能小于0" : "超出了参数的总数"));
		}
		Type genericParameterType = genericParameterTypes[index];
		if (genericParameterType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Type[] parameterArgTypes = aType.getActualTypeArguments();
			for (Type parameterArgType : parameterArgTypes) {
				Class parameterArgClass = (Class) parameterArgType;
				results.add(parameterArgClass);
			}
			return results;
		}
		return results;
	}


	/**
	 * 断言非空
	 * @param dataName
	 * @param values
	 */
	public static void checkNull(String dataName, Object... values){
		if(values == null){
			throw new ValidationException(dataName +" cannot be null");
		}
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			if(value == null){
				throw new ValidationException(dataName +" cannot be null at " + dataName + "[" + i + "]");
			}
		}
	}

}
