package com.redant.core.util;


import com.redant.core.exception.ValidationException;

/**
 * 断言工具类
 * @author gris.wang
 * @create 2017-10-20
 */
public class ValidateUtil {
	
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
