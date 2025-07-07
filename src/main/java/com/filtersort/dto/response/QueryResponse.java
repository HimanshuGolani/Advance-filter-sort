package com.filtersort.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
    private Map<String, List<Map<String, Object>>> groupedRecords;
    private List<Map<String, Object>> sortedRecords;
    private List<Map<String, Object>> records;

    public QueryResponse() {}

    public static QueryResponse withGroupedRecords(Map<String, List<Map<String, Object>>> groupedRecords) {
        QueryResponse response = new QueryResponse();
        response.groupedRecords = groupedRecords;
        return response;
    }

    public static QueryResponse withSortedRecords(List<Map<String, Object>> sortedRecords) {
        QueryResponse response = new QueryResponse();
        response.sortedRecords = sortedRecords;
        return response;
    }

    public static QueryResponse withRecords(List<Map<String, Object>> records) {
        QueryResponse response = new QueryResponse();
        response.records = records;
        return response;
    }

    public Map<String, List<Map<String, Object>>> getGroupedRecords() { return groupedRecords; }
    public void setGroupedRecords(Map<String, List<Map<String, Object>>> groupedRecords) {
        this.groupedRecords = groupedRecords;
    }

    public List<Map<String, Object>> getSortedRecords() { return sortedRecords; }
    public void setSortedRecords(List<Map<String, Object>> sortedRecords) {
        this.sortedRecords = sortedRecords;
    }

    public List<Map<String, Object>> getRecords() { return records; }
    public void setRecords(List<Map<String, Object>> records) { this.records = records; }
}