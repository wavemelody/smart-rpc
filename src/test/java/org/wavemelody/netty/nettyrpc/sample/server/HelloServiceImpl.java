package org.wavemelody.netty.nettyrpc.sample.server;

import org.wavemelody.netty.nettyrpc.client.HelloService;
import org.wavemelody.netty.nettyrpc.client.Person;
import org.wavemelody.netty.nettyrpc.server.RpcService;

@RpcService(HelloService.class)  //指定远程接口
public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "Hello, " + name + "";
	}

	@Override
	public String hello(Person person) {
		return "Hello, " + person.getLastName() + " " + person.getLastName() + "";
	}

}
