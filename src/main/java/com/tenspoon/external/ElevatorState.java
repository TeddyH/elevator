package com.tenspoon.external;

import com.tenspoon.model.Elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ElevatorState {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private HashOperations<String, String, String> hashOps;

    public ElevatorState(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    // 엘리베이터 상태 저장
    public void saveElevatorState(int elevatorId, int currentLocation, int moving, List<Integer> targetLocations) {
        String key = "elevator:" + elevatorId;

        hashOps.put(key, "current_location", String.valueOf(currentLocation));
        hashOps.put(key, "moving", String.valueOf(moving));
        hashOps.put(key, "target_locations", targetLocations.toString());
    }

    // 엘리베이터 상태 읽기
    public Elevator getElevatorState(int elevatorId) {
        String key = "elevator:" + elevatorId;

        // Redis에서 상태 읽기
        String currentLocationStr = hashOps.get(key, "current_location");
        String movingStr = hashOps.get(key, "moving");
        String targetLocationsStr = hashOps.get(key, "target_locations");

        // 상태가 없으면 랜덤 초기화
        if (currentLocationStr == null || currentLocationStr.isEmpty()) {
            Random random = new Random();
            int randomLocation = random.nextInt(100) + 1;
            saveElevatorState(elevatorId, randomLocation, 0, new ArrayList<>());
            return new Elevator(randomLocation);
        }

        // 안전하게 상태 변환
        int currentLocation = parseOrDefault(currentLocationStr, 1); // 기본값 1층
        int moving = parseOrDefault(movingStr, 0); // 기본값 정지
        List<Integer> targetLocations = parseTargetLocations(targetLocationsStr);

        return new Elevator(currentLocation, moving, targetLocations);
    }

    // 엘리베이터 목적지 추가
    public void addTargetLocation(int elevatorId, int targetFloor) {
        String key = "elevator:" + elevatorId;

        String targetLocationsStr = hashOps.get(key, "target_locations");

        // 기존 목적지 목록을 가변 리스트로 변환
        List<Integer> targetLocations = targetLocationsStr.isEmpty()
                ? new ArrayList<>() // 비어있으면 새 리스트 생성
                : Arrays.stream(targetLocationsStr.replace("[", "").replace("]", "").split(", "))
                .map(String::trim) // 문자열 공백 제거
                .map(Integer::valueOf)
                .collect(Collectors.toList()); // Collectors.toList() 사용

        // 새 목적지 추가
        targetLocations.add(targetFloor);

        // Redis에 업데이트
        hashOps.put(key, "target_locations", targetLocations.toString());
    }

    // Redis 문자열을 리스트로 변환
    private List<Integer> parseTargetLocations(String targetLocationsStr) {
        if (targetLocationsStr == null || targetLocationsStr.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(targetLocationsStr.replace("[", "").replace("]", "").split(", "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    // 문자열을 정수로 변환, 실패 시 기본값 반환
    private int parseOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
