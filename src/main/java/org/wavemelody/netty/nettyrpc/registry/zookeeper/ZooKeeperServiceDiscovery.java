package org.wavemelody.netty.nettyrpc.registry.zookeeper;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wavemelody.netty.nettyrpc.registry.Constant;
import org.wavemelody.netty.nettyrpc.registry.ServiceDiscovery;

/**
 * 基于Zookeeper 的服务发现接口实现
 * @author Andy 
 * @version 1.0.0
 * */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);
	
	private String zkAddress;
	
	public ZooKeeperServiceDiscovery(String zkAddress) {
		this.zkAddress = zkAddress;
	}
	
	@Override
	public String discover(String serviceName) {
		//创建 Zookeeper 客户端
		ZkClient zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
		LOGGER.debug("contenect zookeeper {}",zkAddress);
		
		try {
			String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
			if (!zkClient.exists(servicePath)) {
				throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
			}
			List<String> addressList = zkClient.getChildren(servicePath);
			
			if (CollectionUtils.isEmpty(addressList)) {
				throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
			}
			//获取address节点
			String address;
			int size = addressList.size();
			if (size == 1) {
				//若只有一个地址，则获取该地址
				address = addressList.get(0);
			} else {
				address = addressList.get(ThreadLocalRandom.current().nextInt(size));
				LOGGER.debug("get random address node: {}", address);
			}
			String addressPath = servicePath + "/" + address;
			return zkClient.readData(addressPath);
		} finally {
			zkClient.close();
		}
	}

}
