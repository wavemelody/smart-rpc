/**
 * 
 */
package org.wavemelody.netty.nettyrpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author Andy
 * @date 2016年6月18日
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component  // 表明可被Spring扫描
public @interface RpcService {
	/**
	 * 服务接口类
	 * */
	Class<?> value();
	
	/**
	 * 服务版本号
	 * */
	String version() default "";
}
