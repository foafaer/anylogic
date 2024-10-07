package com.dorzhiev.service;

import com.dorzhiev.dto.Request;
import com.dorzhiev.dto.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> cancelMap = new ConcurrentHashMap<>();

    public WorkerService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "factorial.queue", concurrency = "10")
    public void calculateFactorial(Message message) throws IOException {
        log.info("Received calculating task.");

        Request request = getRequest(message);

        BigInteger result = BigInteger.ONE;

        int number = request.getNumber();
        if (number < 0) {
            log.error("Number must be positive!");
            Response response = new Response(null, null, "Number must be positive!");
            convertAndSend(response);
            return;
        }
        try {
            for (int i = 2; i <= number; i++) {
                Thread.sleep(3000); // замедление вычисления
                if (cancelMap.getOrDefault(request.getTaskId(), false)) {
                    Response response = new Response(request.getTaskId(), null, "Cancelled");
                    convertAndSend(response);
                    log.info("Calculation task cancelled.");
                    return;
                }
                result = result.multiply(BigInteger.valueOf(i));
            }
            log.info("Calculation finished successfully.");
            Response response = new Response(request.getTaskId(), result.toString(), "Completed");
            convertAndSend(response);
        } catch (InterruptedException e) {
            log.error("Something went wrong.");
            Response response = new Response(request.getTaskId(), null, "Error: " + e.getMessage());
            convertAndSend(response);
        }
    }

    @RabbitListener(queues = "factorial.cancel.queue")
    public void cancelTask(String taskId) {
        cancelMap.put(taskId, true);
    }

    private Request getRequest(Message message) throws JsonProcessingException {
        Request request = null;
        try {
            request = objectMapper.readValue(message.getBody(), Request.class);
        } catch (IOException ex) {
            Response response = new Response(null, null, ex.getMessage());
            convertAndSend(response);
        }
        return request;
    }


    private void convertAndSend(Response response) throws JsonProcessingException {
        String string = objectMapper.writeValueAsString(response);
        rabbitTemplate.convertAndSend("", "factorial.result.queue", string);
    }
}
