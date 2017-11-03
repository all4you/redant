package com.redant.core.converter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 基本类型和基本类型数组转换器
 * 能够转换的基本类型仅限于com.sitechasia.webx.core.utils.populator.PrimitiveType中所定义的类型
 *
 * @see # com.sitechasia.webx.core.utils.populator.PrimitiveTypeUtil
 * @author gris.wang
 * @create 2017-10-20
 */
public final class PrimitiveConverter extends AbstractConverter {
	
	private static final PrimitiveConverter CONVERTER;

	static {
		CONVERTER = new PrimitiveConverter();
	}

	private static final char[] DIGITAL_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	
	/**
	 * 对基本类型进行类型转换
	 */
	@Override
	@SuppressWarnings("unused")
	protected Object doConvertValue(Object source, Class<?> toType, Object... params) {
		/**
		 * 如果是基础类型，则直接返回
		 */
		if (source != null && (!PrimitiveTypeUtil.isPriType(source.getClass()) || !PrimitiveTypeUtil.isPriType(toType))) {
			return null;
		}

		/**
		 * 如果都是数组类型，则构造数组
		 */
		if (source != null && source.getClass().isArray() && toType.isArray()) {
			Object result;
			Class<?> componentType = toType.getComponentType();
			result = Array.newInstance(componentType, Array.getLength(source));

			for (int i = 0; i < Array.getLength(source); i++) {
				Array.set(result, i, convert(Array.get(source, i),componentType, params));
			}
			return result;
		}
		return doConvert(source, toType);
	}
	
	private boolean isNumberString(String stringValue) {
		if (stringValue == null || stringValue.length() == 0){
			return false;
		}
	
		OUTER: for (char charInString : stringValue.toCharArray()) {
			for (char digit : DIGITAL_CHAR) {
				if (charInString == digit){
					continue OUTER;
				}
			}
			return false;
		}
		return true;
	}
	
	private boolean booleanValue(Object source) {
		if (source == null){
			return false;
		}
		Class<? extends Object> c = source.getClass();
		if (c == Boolean.class){
			return (Boolean) source;
		}
		if (c == String.class) {
			String stringValue = (String) source;
			return !(stringValue.length() == 0
					|| stringValue.equals("0")
					|| stringValue.equalsIgnoreCase("false")
					|| stringValue.equalsIgnoreCase("no")
					|| stringValue.equalsIgnoreCase("f") || stringValue
					.equalsIgnoreCase("n"));
		}
		if (c == Character.class){
			return ((Character) source).charValue() != 0;
		}
		if (source instanceof Number){
			return ((Number) source).doubleValue() != 0;
		}
			
		return true;
	}
	
	private long longValue(Object source) throws NumberFormatException {
		if (source == null){
			return 0L;
		}
		Class<? extends Object> c = source.getClass();
		if (c.getSuperclass() == Number.class){
			return ((Number) source).longValue();
		}
		if (c == Boolean.class){
			return ((Boolean) source).booleanValue() ? 1 : 0;
		}
		if (c == Character.class){
			return ((Character) source).charValue();
		}
			
		String s = stringValue(source, true);
		return (s.length() == 0) ? 0L : Long.parseLong(s);
	}
	
	private double doubleValue(Object source) throws NumberFormatException {
		if (source == null){
			return 0.0;
		}
		Class<? extends Object> c = source.getClass();
		if (c.getSuperclass() == Number.class){
			return ((Number) source).doubleValue();
		}
		if (c == Boolean.class){
			return ((Boolean) source).booleanValue() ? 1 : 0;
		}
		if (c == Character.class){
			return ((Character) source).charValue();
		}
		String s = stringValue(source, true);
		return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
	}
	
	private BigInteger bigIntValue(Object source) throws NumberFormatException {
		if (source == null){
			return BigInteger.valueOf(0L);
		}
		Class<? extends Object> c = source.getClass();
		if (c == BigInteger.class){
			return (BigInteger) source;
		}
		if (c == BigDecimal.class){
			return ((BigDecimal) source).toBigInteger();
		}
		if (c.getSuperclass() == Number.class){
			return BigInteger.valueOf(((Number) source).longValue());
		}
		if (c == Boolean.class){
			return BigInteger.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
		}
		if (c == Character.class){
			return BigInteger.valueOf(((Character) source).charValue());
		}
	
		String s = stringValue(source, true);
		return (s.length() == 0) ? BigInteger.valueOf(0L) : new BigInteger(s);
	}
	
	private BigDecimal bigDecValue(Object source) throws NumberFormatException {
		if (source == null){
			return BigDecimal.valueOf(0L);
		}
		Class<? extends Object> c = source.getClass();
		if (c == BigDecimal.class){
			return (BigDecimal) source;
		}
		if (c == BigInteger.class){
			return new BigDecimal((BigInteger) source);
		}
		if (c.getSuperclass() == Number.class){
			return new BigDecimal(((Number) source).doubleValue());
		}
		if (c == Boolean.class){
			return BigDecimal.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
		}
		if (c == Character.class){
			return BigDecimal.valueOf(((Character) source).charValue());
		}
	
		String s = stringValue(source, true);
		return (s.length() == 0) ? BigDecimal.valueOf(0L) : new BigDecimal(s);
	}
	
	private String stringValue(Object source, boolean trim) {
		String result;
	
		if (source == null) {
			result = null;
		} else {
			result = source.toString();
			if (trim) {
				result = result.trim();
			}
		}
		return result;
	}
	
	private char charValue(Object source) {
		char result;
	
		if (source.getClass() == String.class && ((String) source).length() > 0 && !isNumberString((String) source)){
			result = ((String) source).charAt(0);
		}else{
			result = (char) longValue(source);
		}
	
		return result;
	}
	
	private String stringValue(Object source) {
		return stringValue(source, false);
	}
	
	private Object doConvert(Object source, Class<?> toType) {
		Object result = null;
	
		if (source != null) {
			if ((toType == Integer.class) || (toType == Integer.TYPE)) {
				result = (int) longValue(source);
			}else if ((toType == Double.class) || (toType == Double.TYPE)){
				result = doubleValue(source);
			}else if ((toType == Boolean.class) || (toType == Boolean.TYPE)){
				result = booleanValue(source);
			}else if ((toType == Byte.class) || (toType == Byte.TYPE)){
				result = (byte) longValue(source);
			}else if ((toType == Character.class) || (toType == Character.TYPE)){
				result = charValue(source);
			}else if ((toType == Short.class) || (toType == Short.TYPE)){
				result = (short) longValue(source);
			}else if ((toType == Long.class) || (toType == Long.TYPE)){
				result = longValue(source);
			}else if ((toType == Float.class) || (toType == Float.TYPE)){
				result = (float) doubleValue(source);
			}else if (toType == BigInteger.class){
				result = bigIntValue(source);
			}else if (toType == BigDecimal.class){
				result = bigDecValue(source);
			}else if (toType == String.class){
				result = stringValue(source);
			}
		} else {
			if (toType.isPrimitive()) {
				result = PrimitiveTypeUtil.getPriDefaultValue(toType);
			}
		}
		return result;
	}
	
	public static PrimitiveConverter getInstance(){
		return CONVERTER;
	}

}
