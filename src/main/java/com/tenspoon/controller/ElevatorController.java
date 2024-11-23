package com.tenspoon.controller;

import com.tenspoon.manager.ElevatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elevator")
public class ElevatorController {

    // Spring에서 ElevatorManager를 주입받음
    @Autowired
    private ElevatorManager elevatorManager;

    @PostMapping("/call")
    public String callElevator(@RequestParam int floor) {
        elevatorManager.callElevator(floor); // ElevatorManager 호출

        return "Elevator called to floor " + floor + ".";
    }
}