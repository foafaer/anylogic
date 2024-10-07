package com.dorzhiev.service;

import com.dorzhiev.dto.Response;
import com.dorzhiev.dto.Request;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class ManagerService {

    private static final Logger log = LoggerFactory.getLogger(ManagerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ManagerService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public String startFactorialCalculation(Integer number) throws JsonProcessingException {
        String taskId = UUID.randomUUID().toString();
        Request request = new Request(taskId, number);
        String requestAsString = objectMapper.writeValueAsString(request);
        rabbitTemplate.convertAndSend("", "factorial.queue", requestAsString);
        return taskId;
    }

    public void cancelFactorialCalculation(String taskId) {
        rabbitTemplate.convertAndSend("", "factorial.cancel.queue", taskId);
    }

    @RabbitListener(queues = "factorial.result.queue")
    public void receiveResult(Message message) throws IOException {
        log.info("Result received.");
        Response response = objectMapper.readValue(message.getBody(), Response.class);
        System.out.println("Result received: " + response.toString());
    }
}
