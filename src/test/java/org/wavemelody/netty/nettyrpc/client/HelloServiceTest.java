package org.wavemelody.netty.nettyrpc.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wavemelody.netty.nettyrpc.client.proxy.RpcProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring-client.xml")
public class HelloServiceTest {
	@Autowired
	private RpcProxy rpcProxy;
	
	@Test
	public void helloTest(){
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello("Andy");
		System.out.println(result);
	}
	
	@Test
	public void helloTest2(){
		HelloService helloService = rpcProxy.create(HelloService.class);
		Person person = new Person();
		person.setFirstName("aa");
		person.setLastName("bb");
		String result = helloService.hello(person);
		System.out.println(result);
	}
}
