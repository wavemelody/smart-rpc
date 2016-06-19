package org.wavemelody.netty.nettyrpc.protocol.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 序列化工具类（基于Protostuff实现）
 * @author Andy
 * @version 1.0.0
 * */
public class SerializationUtil {
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
	
	private static Objenesis objenesis = new ObjenesisStd(true);
	
	public SerializationUtil() {
		
	}
	
	public static <T> byte[] serialize(T object){
		@SuppressWarnings("unchecked")
		Class<T> clz = (Class<T>) object.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			Schema<T> schema = getSchema(clz);
			return ProtostuffIOUtil.toByteArray(object, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		} finally {
			buffer.clear();
		}
	}
	
	public static <T> T deserialize(byte[] data, Class<T> clz){
		try {
			T message = objenesis.newInstance(clz);
			Schema<T> schema = getSchema(clz);
			ProtostuffIOUtil.mergeFrom(data, message, schema);
			return message;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	private static <T> Schema<T> getSchema(Class<T> clz){
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) cachedSchema.get(clz);
		if(schema == null){
			schema = RuntimeSchema.createFrom(clz);
			cachedSchema.put(clz, schema);
		}
		return schema;
	}
}
