package com.tenspoon.controller;

import com.tenspoon.manager.ElevatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elevator")
public class ElevatorController {

    @Autowired
    private ElevatorManager elevatorManager;

    @PostMapping("/call")
    public ResponseEntity<String> callElevator(@RequestParam int floor) {
        try {
            elevatorManager.callElevator(floor);
            return ResponseEntity.status(HttpStatus.OK).body("Elevator successfully called to floor " + floor + ".");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid floor number: " + floor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while calling the elevator.");
        }
    }
}