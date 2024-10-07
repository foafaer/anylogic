package com.dorzhiev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Request implements Serializable {
    @JsonProperty("taskId")
    private String taskId;
    @JsonProperty("number")
    private Integer number;

    public Request(String taskId, Integer number) {
        this.taskId = taskId;
        this.number = number;
    }

    public Request() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
