package org.wavemelody.netty.nettyrpc.sample.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcBootstrap.class);
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		LOGGER.debug("start server");
		System.out.println("start server");
		new ClassPathXmlApplicationContext("spring.xml");
	}

}
