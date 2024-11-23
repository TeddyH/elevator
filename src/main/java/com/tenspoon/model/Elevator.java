package com.tenspoon.model;

import java.util.ArrayList;
import java.util.List;

public class Elevator {
    private int currentLocation; // 현재 위치 (1 ~ 100)
    private int moving; // 이동 상태 (-1:아래로 이동 중, 0:정지 중, 1:위로 이동 중)
    private List<Integer> targetLocation; // 목적지 (1 ~ 100 배열)

    public Elevator(int currentLocation) {
        this.currentLocation = currentLocation;
        this.moving = 0;
        this.targetLocation = new ArrayList<>();
    }

    public Elevator(int currentLocation, int moving, List<Integer> targetLocation) {
        this.currentLocation = currentLocation;
        this.moving = moving;
        this.targetLocation = targetLocation;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(int currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getMoving() {
        return moving;
    }

    public void setMoving(int moving) {
        this.moving = moving;
    }

    public List<Integer> getTargetLocation() {
        return targetLocation;
    }

    public void addTargetLocation(int location) {
        if (!targetLocation.contains(location)) {
            targetLocation.add(location);
        }
    }

    public void setTargetLocation(List<Integer> targetLocation) {
        this.targetLocation = targetLocation;
    }
}