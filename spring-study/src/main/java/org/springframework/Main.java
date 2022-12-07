package org.springframework;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.entity.User;

public class Main {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:Bean.xml");

		User userInfo = applicationContext.getBean(User.class);
		System.out.println(userInfo.toString());
		System.out.println("Hello world!");
	}
}