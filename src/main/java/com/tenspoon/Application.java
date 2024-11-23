package com.tenspoon;

import com.tenspoon.manager.ElevatorManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		int elevatorCount = 3;

		ApplicationContext context = SpringApplication.run(Application.class, args);

		// Spring 컨텍스트에서 ElevatorManager 빈 가져오기
		ElevatorManager elevatorManager = context.getBean(ElevatorManager.class);
		elevatorManager.initializeElevators(elevatorCount); // 엘리베이터 n대 초기화
	}
}