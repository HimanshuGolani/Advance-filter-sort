package com.filtersort.dto.request;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class RecordInsertRequest {

    @NotNull
    private Map<String, Object> data = new HashMap<>();

    public <K, V> RecordInsertRequest(Map<K, V> name) {
    }

    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }

    @JsonAnySetter
    public void setData(String key, Object value) {
        this.data.put(key, value);
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}