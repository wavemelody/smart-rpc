package org.wavemelody.netty.nettyrpc.protocol.struct;

/**
 * 封装RPC请求
 * @author Andy
 * @version 1.0.0
 * */
public final class RpcRequest {
	private String  requestd;
	private String interfaceName;
	private String serviceVersion;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parametes;
	public final String getRequestd() {
		return requestd;
	}
	public final void setRequestd(String requestd) {
		this.requestd = requestd;
	}
	public final String getInterfaceName() {
		return interfaceName;
	}
	public final void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public final String getMethodName() {
		return methodName;
	}
	public final void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public final Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public final void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public final Object[] getParametes() {
		return parametes;
	}
	public final void setParametes(Object[] parametes) {
		this.parametes = parametes;
	}
	public final String getServiceVersion() {
		return serviceVersion;
	}
	public final void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
}
