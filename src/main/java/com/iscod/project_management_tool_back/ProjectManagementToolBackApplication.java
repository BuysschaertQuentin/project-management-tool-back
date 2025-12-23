package com.iscod.project_management_tool_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProjectManagementToolBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectManagementToolBackApplication.class, args);
	}
}
