package com.sdcloud.biz.core.service.main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupServiceProvider {
	public static void main(String[] args){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-context.xml"});
        context.start();
        System.out.println(" provider is setting up!! ");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
