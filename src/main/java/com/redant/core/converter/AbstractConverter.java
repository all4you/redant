package com.redant.core.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 转换器的抽象实现类
 * @author gris.wang
 * @create 2017-10-20
 */
public abstract class AbstractConverter implements Converter {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 实现了TypeConverter中的相同方法
	 */
	@Override
	public Object convert(Object source, Class<?> toType, Object... parmas) {
		
		/**
		 * 如果对象本身已经是所指定的类型则不进行转换直接返回
		 * 如果对象能够被复制，则返回复制后的对象
		 */
		if (source != null && toType.isInstance(source)) {
			if (source instanceof Cloneable) {
				if(source.getClass().isArray() && source.getClass().getComponentType() == String.class){
					// 字符串数组虽然是Cloneable的子类，但并没有clone方法
					return source;
				}
				try {
					Method m = source.getClass().getDeclaredMethod("clone", new Class[0]);
					m.setAccessible(true);
					return m.invoke(source, new Object[0]);
				} catch (Exception e) {
					logger.debug("Can not clone object " + source, e);
				}
			}

			return source;
		}

		/**
		 * 如果需要转换，且value为String类型并且长度为0，则按照null值进行处理
		 */
		if (source != null && source instanceof String && ((String)source).length() == 0) {
			source = null;
		}

		/**
		 * 不对Annotation, Interface,
		 * Enummeration类型进行转换。
		 */
		if (toType == null || (source == null && !toType.isPrimitive())
				|| toType.isInterface() || toType.isAnnotation()
				|| toType.isEnum()) {
			return null;
		}

		return doConvertValue(source, toType);
	}

	/**
	 * 需要被子类所实现的转换方法
	 * @param source 需要进行类型转换的对象
	 * @param toType　需要被转换成的类型
	 * @param params　转值时需要提供的可选参数
	 * @return　转换后所生成的对象，如果不能够进行转换则返回null
	 */
	protected abstract Object doConvertValue(Object source, Class<?> toType, Object... params);

}
