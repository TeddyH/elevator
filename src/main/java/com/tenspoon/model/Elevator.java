package com.tenspoon.model;

import java.util.ArrayList;
import java.util.List;

public class Elevator {
    private int currentLocation; // 현재 위치 (1 ~ 100)
    private int moving; // 이동 상태 (-1:아래로 이동 중, 0:정지 중, 1:위로 이동 중)
    private List<Integer> targetLocation; // 목적지 (1 ~ 100 배열)

    // 생성자
    public Elevator(int currentLocation) {
        this.currentLocation = currentLocation;
        this.moving = 0; // 정지 상태
        this.targetLocation = new ArrayList<>();
    }

    // 현재 위치
    public int getCurrentLocation() {
        return currentLocation;
    }

    // 이동 상태
    public int getMoving() {
        return moving;
    }

    // 목적지 목록
    public List<Integer> getTargetLocation() {
        return targetLocation;
    }

    // 현재 위치 설정
    public void setCurrentLocation(int currentLocation) {
        this.currentLocation = currentLocation;
    }

    // 이동 상태 설정
    public void setMoving(int moving) {
        this.moving = moving;
    }

    // 목적지 추가
    public void addTargetLocation(int location) {
        this.targetLocation.add(location);
    }

    // 목적지 제거
    public void removeTargetLocation(int location) {
        this.targetLocation.remove(Integer.valueOf(location));
    }
}