package com.tenspoon.controller;

import com.tenspoon.manager.ElevatorManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elevator")
public class ElevatorController {

    @PostMapping("/call")
    public String callElevator(@RequestParam int floor) {
        ElevatorManager elevatorManager = ElevatorManager.getInstance();
        elevatorManager.callElevator(floor); // 변경된 메서드 호출

        return "Elevator called to floor " + floor + ".";
    }
}