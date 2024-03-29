package com.fisclouds;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.fisclouds.service.ProcessorTasks;

@SpringBootApplication
@ComponentScan(basePackages = "com") 
public class GenerateSqlApplication {
	
	

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext run = SpringApplication.run(GenerateSqlApplication.class, args);
		
		System.out.println("Application is Running *******************");
		ProcessorTasks bean = run.getBean(ProcessorTasks.class);
		System.out.println(bean);

	}

}
