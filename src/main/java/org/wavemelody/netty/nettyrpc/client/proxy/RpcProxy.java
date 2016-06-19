package org.wavemelody.netty.nettyrpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wavemelody.netty.nettyrpc.client.RpcClient;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcRequest;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcResponse;
import org.wavemelody.netty.nettyrpc.registry.ServiceDiscovery;
import org.wavemelody.netty.nettyrpc.util.StringUtils;


public class RpcProxy {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);
	private String serviceAddress;
	
	private ServiceDiscovery serviceDiscovery;
	
	public RpcProxy(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	public RpcProxy(ServiceDiscovery serviceDiscovery){
		this.serviceDiscovery = serviceDiscovery;
	}
	
	
	public <T> T create(final Class<?> interfaceClass){
		return create(interfaceClass,"");
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create(final Class<?> interfaceClass,final String serviceVersion){
		//创建动态代理对象
		return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), 
							   new Class<?>[]{interfaceClass}, 
							   new InvocationHandler() {
								
								@Override
								public Object invoke(Object proxy, Method method, Object[] parametes) throws Throwable {
									//创建 RPC 请求对象并设置请求属性
									RpcRequest request = new RpcRequest();
									request.setRequestd(UUID.randomUUID().toString());
									request.setInterfaceName(method.getDeclaringClass().getName());
									request.setServiceVersion(serviceVersion);
									request.setMethodName(method.getName());
									request.setParameterTypes(method.getParameterTypes());
									request.setParametes(parametes);
									
									//获取 RPC 服务器地址
									if(serviceDiscovery != null){
										String serviceName = interfaceClass.getName();
										if(StringUtils.isNotEmpty(serviceVersion)){
											serviceName += "-" + serviceVersion;
										}
										serviceAddress = serviceDiscovery.discover(serviceName);
										LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
									}
									
									if(StringUtils.isEmpty(serviceAddress)){
										throw new RuntimeException("server address is empty");
									}
									
									//从 RPC 服务地质处中解析主机名与端口号
									String[] array  = StringUtils.split(serviceAddress, ":");
									String host = array[0];
									int port = Integer.parseInt(array[1]);
									
									// 创建 RPC 客户端对象并发送 RPC 请求
									RpcClient client = new RpcClient(host, port);
									long time = System.currentTimeMillis();
									RpcResponse response = client.send(request);
									LOGGER.debug("time: {}ms", System.currentTimeMillis() - time);
									if(response == null){
										 throw new RuntimeException("response is null");
									}
									
									//返回响应结果
									if(response.hasException()){
										throw response.getException();
									}else{
										return response.getResult();
									}
								}
							});
	}
}
