package org.wavemelody.netty.nettyrpc.util;

/**
 * 字符串工具类
 * @author Andy
 * @version 1.0.0
 * */
public class StringUtils {
	/**
	 * 判断字符串是否为空
	 * */
	public static boolean isEmpty(String str){
		return str == null || str.trim().equals("");
	}
	
	/**
	 * 判断字符串不为空
	 * */
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	public static String[] split(String str,String delimiter){
		return org.springframework.util.StringUtils.split(str, delimiter);
	}
}
