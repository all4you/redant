package com.redant.core.serialize;

public interface Serializer {

	/**
	 * 序列化
	 * @param obj
	 * @return
	 */
	<T> byte[] serialize(T obj);
	
	/**
	 * 反序列化
	 * @param bytes
	 * @param cls
	 * @return
	 */
	<T> T deserialize(byte[] bytes, Class<T> cls);
}
