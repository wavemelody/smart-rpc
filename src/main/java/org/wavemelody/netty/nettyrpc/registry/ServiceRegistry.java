package org.wavemelody.netty.nettyrpc.registry;

/**
 * 服务注册接口
 * */
public interface ServiceRegistry {

	/**
	 *  注册服务名称与服务地址
	 *  @param serviceName   服务名称
	 *  @parm serviceAddress 服务地址
	 * */
	
	void register(String serviceName, String serviceAddress);
}
