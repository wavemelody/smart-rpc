package org.wavemelody.netty.nettyrpc.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcRequest;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcResponse;
import org.wavemelody.netty.nettyrpc.util.StringUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
	private static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
	
	private final Map<String, Object> handlerMap;
	
	public RpcServerHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
		// 创建并初始化RPC响应对象
		RpcResponse response = new RpcResponse();
		response.setRequestId(request.getRequestd());
		
		try {
			Object result = handle(request);
			response.setResult(result);
		} catch (Exception e) {
			logger.error("handle result failure ", e);
			response.setException(e);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private Object handle(RpcRequest request) throws InvocationTargetException{
		String serviceName = request.getInterfaceName();
		String serviceVersion = request.getServiceVersion();
		if(StringUtils.isNotEmpty(serviceVersion)){
			serviceName += "-" + serviceVersion;
		}
		Object serviceBean = handlerMap.get(serviceName);
		if(serviceBean == null){
			throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
		}
		
		//获取反射调用所需参数
		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParametes();
		
		
	       // 执行反射调用
//      Method method = serviceClass.getMethod(methodName, parameterTypes);
//      method.setAccessible(true);
//      return method.invoke(serviceBean, parameters);
		
		//使用CGLib执行反射调用
		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
		return serviceFastMethod.invoke(serviceBean, parameters);
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("server caught exception :" + cause.getMessage());
		ctx.close();
	}

}
