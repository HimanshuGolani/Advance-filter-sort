package com.filtersort.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordInsertResponse {
    private String message;
    private String dataset;

    @JsonProperty("recordId")
    private Long recordId;

    public RecordInsertResponse() {}

    public RecordInsertResponse(String message, String dataset, Long recordId) {
        this.message = message;
        this.dataset = dataset;
        this.recordId = recordId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDataset() { return dataset; }
    public void setDataset(String dataset) { this.dataset = dataset; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
}