package com.dorzhiev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Response implements Serializable {
    @JsonProperty("taskId")
    private String taskId;
    @JsonProperty("result")
    private String result;
    @JsonProperty("status")
    private String status;

    public Response(String taskId, String result, String status) {
        this.taskId = taskId;
        this.result = result;
        this.status = status;
    }

    public Response() {

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Response{" +
                "taskId='" + taskId + '\'' +
                ", result='" + result + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
