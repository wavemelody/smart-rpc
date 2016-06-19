package org.wavemelody.netty.nettyrpc.client;

public interface HelloService {
	String hello(String name);
	String hello(Person person);
}
