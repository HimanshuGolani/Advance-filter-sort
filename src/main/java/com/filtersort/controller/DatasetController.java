package com.filtersort.controller;

import com.filtersort.dto.request.RecordInsertRequest;
import com.filtersort.dto.response.QueryResponse;
import com.filtersort.dto.response.RecordInsertResponse;
import com.filtersort.service.DatasetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private static final Logger log = LoggerFactory.getLogger(DatasetController.class);

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<RecordInsertResponse> insertRecord(
            @PathVariable String datasetName,
            @Valid @RequestBody RecordInsertRequest request) {

        log.info("Received request to insert record into dataset: {}", datasetName);
        RecordInsertResponse response = datasetService.insertRecord(datasetName, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    public ResponseEntity<QueryResponse> queryDataset(
            @PathVariable String datasetName,
            @RequestParam MultiValueMap<String, String> params) {

        log.info("Received query request for dataset: {} with params: {}", datasetName, params);
        QueryResponse response = datasetService.queryDataset(datasetName, params);

        return ResponseEntity.ok(response);
    }
}