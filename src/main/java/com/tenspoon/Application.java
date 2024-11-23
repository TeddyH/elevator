package com.tenspoon;

import com.tenspoon.manager.ElevatorManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		int elevatorCount = 3;

		SpringApplication.run(Application.class, args);

		ElevatorManager elevatorManager = ElevatorManager.getInstance();
		elevatorManager.initializeElevators(elevatorCount); // 엘리베이터 n대 초기화
	}
}