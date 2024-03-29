package com.fisclouds.entity;

import org.springframework.stereotype.Component;

@Component
public class ConnectionDTO {



    private String sourceProcessorId;


    private String destinationProcessorId;


    private String processGroupId;

    @Override
    public String toString() {
        return "ConnectionDTO{" +
                "sourceProcessorId='" + sourceProcessorId + '\'' +
                ", destinationProcessorId='" + destinationProcessorId + '\'' +
                ", processGroupId='" + processGroupId + '\'' +
                '}';
    }

    public ConnectionDTO() {
    }

    public ConnectionDTO(String sourceProcessorId, String destinationProcessorId, String processGroupId) {
        this.sourceProcessorId = sourceProcessorId;
        this.destinationProcessorId = destinationProcessorId;
        this.processGroupId = processGroupId;
    }

    public String getSourceProcessorId() {
        return sourceProcessorId;
    }

    public void setSourceProcessorId(String sourceProcessorId) {
        this.sourceProcessorId = sourceProcessorId;
    }

    public String getDestinationProcessorId() {
        return destinationProcessorId;
    }

    public void setDestinationProcessorId(String destinationProcessorId) {
        this.destinationProcessorId = destinationProcessorId;
    }

    public String getProcessGroupId() {
        return processGroupId;
    }

    public void setProcessGroupId(String processGroupId) {
        this.processGroupId = processGroupId;
    }
}
