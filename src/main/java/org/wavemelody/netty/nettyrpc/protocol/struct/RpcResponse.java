package org.wavemelody.netty.nettyrpc.protocol.struct;

/**
 * 分装RPC响应
 * @author Andy
 * @version 1.0.0
 * */
public final class RpcResponse {
	private String requestId;
	private Exception exception;
	private Object result;
	
	public boolean hasException(){
		return exception != null;
	}

	public final String getRequestId() {
		return requestId;
	}

	public final void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public final Throwable getException() {
		return exception;
	}

	public final void setException(Exception exception) {
		this.exception = exception;
	}

	public final Object getResult() {
		return result;
	}

	public final void setResult(Object result) {
		this.result = result;
	}
}
