package com.redant.core.converter;

/**
 * 类型转换器所需要实现的总接口。TypeConverter中有唯一的一个方法,实现类请特别注意方法所需要返回的值。
 * @author houyi.wh
 * @date 2017-10-20
 */
public interface Converter {

	/**
	 * 类型转换
	 * @param source 需要被转换的值
	 * @param toType 需要被转换成的类型
	 * @param params 转值时需要提供的可选参数
	 * @return 经转换过的类型，如果实现类没有能力进行所指定的类型转换，应返回null
	 */
	Object convert(Object source, Class<?> toType, Object... params);
	
}
