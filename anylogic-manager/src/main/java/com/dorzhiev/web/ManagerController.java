package com.dorzhiev.web;

import com.dorzhiev.service.ManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/task")
public class ManagerController {

    private static final Logger log = LoggerFactory.getLogger(ManagerController.class);

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @PostMapping
    public ResponseEntity<String> calculateFactorial(@RequestParam Integer number) throws JsonProcessingException {
        log.info("Start calculation.");
        String taskId = managerService.startFactorialCalculation(number);
        return ResponseEntity.ok(taskId);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> cancelFactorial(@PathVariable String taskId) {
        log.info("Cancel factorial calculating.");
        managerService.cancelFactorialCalculation(taskId);
        return ResponseEntity.ok().build();
    }
}
