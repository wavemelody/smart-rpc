package org.wavemelody.netty.nettyrpc.server;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wavemelody.netty.nettyrpc.protocol.codec.RpcDecoder;
import org.wavemelody.netty.nettyrpc.protocol.codec.RpcEncoder;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcRequest;
import org.wavemelody.netty.nettyrpc.protocol.struct.RpcResponse;
import org.wavemelody.netty.nettyrpc.registry.ServiceRegistry;
import org.wavemelody.netty.nettyrpc.util.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
public class RpcServer implements ApplicationContextAware, InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
	
	private String serviceAddress;
	private ServiceRegistry serviceRegistry;
	
	/**
	 * 存放服务名与服务对下列之间的映射
	 * */
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	
	
	public RpcServer(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	
	public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
		this.serviceAddress = serviceAddress;
		this.serviceRegistry = serviceRegistry;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		if(MapUtils.isNotEmpty(serviceBeanMap)){
			for(Object serviceBean : serviceBeanMap.values()){
				RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
				String serviceName = rpcService.value().getName();
				String serviceVersion = rpcService.version();
				if(StringUtils.isNotEmpty(serviceVersion)){
					serviceName += "-" + serviceVersion;
				}
				handlerMap.put(serviceName, serviceBean);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			//创建并初始化Netty服务端BootStrap对象
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new RpcDecoder(RpcRequest.class));  //解码RPC请求
					pipeline.addLast(new RpcEncoder(RpcResponse.class)); //编码RPC响应
					pipeline.addLast(new RpcServerHandler(handlerMap));  //处理RPC请求
				}
				
			});
			
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			//获取RPC服务器的IP地址与端口号
			String[] addressArray = StringUtils.split(serviceAddress, ":");
			String ip = addressArray[0];
			int port = Integer.parseInt(addressArray[1]);
			
			//启动RPC服务
			ChannelFuture future = bootstrap.bind(ip, port).sync();	
			// 注册 RPC 服务地址
			if(serviceRegistry != null){
				for(String interfaceName : handlerMap.keySet()){
					serviceRegistry.register(interfaceName, serviceAddress);
					logger.debug("register service: {} => {}", interfaceName, serviceAddress);
				}
			}
			
			logger.debug("server started on port {}", port);
			 // 关闭 RPC 服务器
			 future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}

}
