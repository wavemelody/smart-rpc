package org.wavemelody.netty.nettyrpc.registry.zookeeper;

import java.util.Date;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wavemelody.netty.nettyrpc.registry.Constant;
import org.wavemelody.netty.nettyrpc.registry.ServiceRegistry;

/**
 * 基于 ZooKeeper 的服务注册接口实现
 *
 * @author Andy
 * @version 1.0.0
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);
	private final ZkClient zkClient;
	
	public ZooKeeperServiceRegistry(String zkAddress) {
		System.out.println(new Date() + "连接:" + zkAddress + "start");
		//创建Zookeeper 客户端
		zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT);
		
		System.out.println(new Date() + "成功连接:" + zkAddress);
		LOGGER.debug("connect zookeeper");
	}
	
	@Override
	public void register(String serviceName, String serviceAddress) {
		// 创建 registry 节点（持久）
		String registryPath = Constant.ZK_REGISTRY_PATH;
		if(!zkClient.exists(registryPath)){
			zkClient.createPersistent(registryPath);
			LOGGER.debug("create registry node: {}", registryPath);
		}
		
		// 创建 service 节点（持久）
		String servicePath = registryPath + "/" + serviceName;
		if(!zkClient.exists(servicePath)){
			zkClient.createPersistent(servicePath);
			LOGGER.debug("create service node: {}", servicePath);
		}
		
		//创建address节点（临时）
		String addressPath = servicePath + "/addres-";
		String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
		LOGGER.debug("create address node: {}", addressNode);
	}

}
