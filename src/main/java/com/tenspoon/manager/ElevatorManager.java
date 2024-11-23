package com.tenspoon.manager;

import com.tenspoon.model.Elevator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ElevatorManager {
    private static ElevatorManager instance;
    private Map<Integer, Elevator> elevators;

    // Singleton 패턴 사용
    private ElevatorManager() {
        this.elevators = new HashMap<>();
    }

    public static synchronized ElevatorManager getInstance() {
        if (instance == null) {
            instance = new ElevatorManager();
        }
        return instance;
    }

    // 엘리베이터 초기화
    public void initializeElevators(int n) {
        Random random = new Random();
        elevators.clear();

        for (int i = 1; i <= n; i++) {
            // 1부터 100까지 랜덤 위치 생성
            int randomLocation = random.nextInt(100) + 1;
            elevators.put(i, new Elevator(randomLocation));
        }

        System.out.println("Elevators initialized.");
        printElevatorsStatus();
    }

    public Elevator getElevator(int id) {
        return elevators.get(id);
    }

    public Map<Integer, Elevator> getElevators() {
        return elevators;
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
        System.out.println("Selected Elevator: #" + selectedElevatorId);

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
        int currentLocation = selectedElevator.getCurrentLocation();

        // 엘리베이터 이동방향 1:위, 0:정지, -1:아래
        if (floor > currentLocation) {
            selectedElevator.setMoving(1);
        } else if (floor < currentLocation) {
            selectedElevator.setMoving(-1);
        } else {
            selectedElevator.setMoving(0);
        }

        // 목적지 추가
        selectedElevator.addTargetLocation(floor);

        // 엘리베이터가 호출되면 매초 한 층씩 이동 처리 하고 상태를 출력
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            moveElevators();
            printElevatorsStatus();
            if (selectedElevator.getTargetLocation().isEmpty()) {
                selectedElevator.setMoving(0); // 이동 중지
                scheduler.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // 엘리베이터 이동
    private void moveElevators() {
        for (Elevator elevator : elevators.values()) {
            if (!elevator.getTargetLocation().isEmpty()) {
                // 현재 이동 방향에 따라 목적지 리스트 정렬
                if (elevator.getMoving() >= 0) { // 올라가는 경우
                    elevator.getTargetLocation().sort(Integer::compareTo);
                } else { // 내려가는 경우
                    elevator.getTargetLocation().sort((a, b) -> b - a);
                }

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
                    elevator.getTargetLocation().remove(0); // 목표 제거
                }
            }
        }
    }

    // 엘리베이터 한 줄로 출력
    private void printElevatorsStatus() {
        StringBuilder sb = new StringBuilder("Elevator Status: ");
        for (Map.Entry<Integer, Elevator> entry : elevators.entrySet()) {
            int id = entry.getKey();
            Elevator elevator = entry.getValue();
            int currentLocation = elevator.getCurrentLocation();

            sb.append(String.format("#%d(%d, %d, %s) ",
                    id,
                    currentLocation,
                    elevator.getMoving(),
                    elevator.getTargetLocation().toString()
            ));
        }
        System.out.println(sb.toString().trim());
    }
}