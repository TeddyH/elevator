package com.tenspoon.manager;

import com.tenspoon.external.ElevatorState;
import com.tenspoon.model.Elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ElevatorManager {
    private Map<Integer, Elevator> elevators;

    @Autowired
    private ElevatorState elevatorState;

    public ElevatorManager() {
        this.elevators = new HashMap<>();
    }

    // 엘리베이터 초기화, redis에 정보가 있으면 가져오고 없으면 랜덤하게 위치를 생성
    public void initializeElevators(int n) {
        for (int i = 1; i <= n; i++) {
            Elevator elevator = elevatorState.getElevatorState(i);
            elevators.put(i, elevator);
        }

        System.out.println("Elevators initialized with Redis.");
        printElevatorsStatus();
    }

    // 적절한 엘리베이터 선택
    public int selectElevator(int buttonFloor) {
        int selectedElevatorId = -1;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<Integer, Elevator> entry : elevators.entrySet()) {
            int id = entry.getKey();
            Elevator elevator = entry.getValue();
            int currentLocation = elevator.getCurrentLocation();
            int moving = elevator.getMoving();
            int distance = Math.abs(buttonFloor - currentLocation);

            boolean isDirectionValid = (buttonFloor > currentLocation && moving >= 0) ||
                    (buttonFloor < currentLocation && moving <= 0) ||
                    moving == 0;

            if (isDirectionValid && distance < minDistance) {
                minDistance = distance;
                selectedElevatorId = id;
            }
        }
        System.out.println("(!) Elevator Selected: #" + selectedElevatorId);

        return selectedElevatorId;
    }

    // 엘리베이터 호출
    public void callElevator(int floor) {
        System.out.println("(!) Elevator called at floor " + floor + ".");
        int selectedElevatorId = selectElevator(floor);

        if (selectedElevatorId == -1) {
            System.out.println("No available elevator for floor: " + floor);
            return;
        }

        Elevator selectedElevator = elevators.get(selectedElevatorId);
        selectedElevator.addTargetLocation(floor);

        // 목적지 정렬 (현재 위치 기준으로 가까운 순서대로)
        sortTargetFromNearestFloor(selectedElevator);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            moveElevator(selectedElevatorId);
            printElevatorsStatus();

            if (selectedElevator.getTargetLocation().isEmpty()) {
                selectedElevator.setMoving(0); // 정지 상태로 전환
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void moveElevator(int elevatorId) {
        Elevator elevator = elevators.get(elevatorId);
        if (!elevator.getTargetLocation().isEmpty()) {
            int nextTarget = elevator.getTargetLocation().get(0);
            int currentLocation = elevator.getCurrentLocation();

            if (currentLocation < nextTarget) {
                elevator.setCurrentLocation(currentLocation + 1);
                elevator.setMoving(1); // 위로 이동
            } else if (currentLocation > nextTarget) {
                elevator.setCurrentLocation(currentLocation - 1);
                elevator.setMoving(-1); // 아래로 이동
            } else {
                elevator.setMoving(0); // 도착
                elevator.getTargetLocation().remove(0); // 도착한 목적지 제거
                System.out.println("(!) Elevator #" + elevatorId + " arrived at floor " + currentLocation + ".");
            }

            // Redis에 상태 저장
            elevatorState.saveElevatorState(elevatorId, elevator.getCurrentLocation(), elevator.getMoving(), elevator.getTargetLocation());
        }
    }

    // 전체 엘리베이터 상태를 한 줄로 출력
    private void printElevatorsStatus() {
        StringBuilder sb = new StringBuilder("Elevator Status: ");
        for (Map.Entry<Integer, Elevator> entry : elevators.entrySet()) {
            int id = entry.getKey();
            Elevator elevator = entry.getValue();
            sb.append(String.format("#%d(%d, %d, %s) ",
                    id,
                    elevator.getCurrentLocation(),
                    elevator.getMoving(),
                    elevator.getTargetLocation().toString()
            ));
        }
        System.out.println(sb.toString().trim());
    }

    private void sortTargetFromNearestFloor(Elevator elevator) {
        int currentLocation = elevator.getCurrentLocation();
        elevator.getTargetLocation().sort((a, b) -> Integer.compare(Math.abs(a - currentLocation), Math.abs(b - currentLocation)));
    }

}