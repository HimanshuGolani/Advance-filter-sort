package com.filtersort.service;

import com.filtersort.dto.request.RecordInsertRequest;
import com.filtersort.dto.response.QueryResponse;
import com.filtersort.dto.response.RecordInsertResponse;
import com.filtersort.entity.Dataset;
import com.filtersort.entity.DatasetRecord;
import com.filtersort.exception.custom.DatasetNotFoundException;
import com.filtersort.repository.DatasetRecordRepository;
import com.filtersort.repository.DatasetRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DatasetService {

    private static final Logger log = LoggerFactory.getLogger(DatasetService.class);

    private final DatasetRepository datasetRepository;
    private final DatasetRecordRepository recordRepository;
    private final QuerySpecificationService specificationService;

    public DatasetService(DatasetRepository datasetRepository,
                          DatasetRecordRepository recordRepository,
                          QuerySpecificationService specificationService) {
        this.datasetRepository = datasetRepository;
        this.recordRepository = recordRepository;
        this.specificationService = specificationService;
    }

    public RecordInsertResponse insertRecord(String datasetName, @Valid RecordInsertRequest request) {
        log.info("Inserting record into dataset: {}", datasetName);

        Dataset dataset = datasetRepository.findByName(datasetName)
                .orElseGet(() -> {
                    Dataset newDataset = new Dataset(datasetName);
                    return datasetRepository.save(newDataset);
                });

        DatasetRecord record = new DatasetRecord(dataset, request.getData());
        DatasetRecord savedRecord = recordRepository.save(record);

        log.info("Record inserted successfully with ID: {}", savedRecord.getId());
        return new RecordInsertResponse("Record added successfully", datasetName, savedRecord.getId());
    }

    @Transactional(readOnly = true)
    public QueryResponse queryDataset(String datasetName, MultiValueMap<String, String> params) {
        log.info("Querying dataset: {} with params: {}", datasetName, params);

        if (!datasetRepository.existsByName(datasetName)) {
            throw new DatasetNotFoundException("Dataset not found: " + datasetName);
        }

        String groupBy = params.getFirst("groupBy");
        String sortBy = params.getFirst("sortBy");
        String order = params.getFirst("order");

        List<DatasetRecord> records = getFilteredRecords(datasetName, params);
        List<Map<String, Object>> recordData = records.stream()
                .map(DatasetRecord::getJsonData)
                .collect(Collectors.toList());

        if (groupBy != null && !groupBy.isEmpty()) {
            return QueryResponse.withGroupedRecords(groupRecords(recordData, groupBy));
        } else if (sortBy != null && !sortBy.isEmpty()) {
            return QueryResponse.withSortedRecords(sortRecords(recordData, sortBy, order));
        } else {
            return QueryResponse.withRecords(recordData);
        }
    }

    private List<DatasetRecord> getFilteredRecords(String datasetName, MultiValueMap<String, String> params) {
        // Create the base specification for dataset name filtering
        Specification<DatasetRecord> datasetSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dataset").get("name"), datasetName);

        // Get additional filter specifications
        Specification<DatasetRecord> filterSpec = specificationService.parseSearchParams(params);

        // Combine specifications using and() method
        Specification<DatasetRecord> combinedSpec = datasetSpec.and(filterSpec);

        return recordRepository.findAll(combinedSpec);
    }

    private Map<String, List<Map<String, Object>>> groupRecords(List<Map<String, Object>> records, String groupBy) {
        return records.stream()
                .filter(record -> record.containsKey(groupBy))
                .collect(Collectors.groupingBy(
                        record -> String.valueOf(record.get(groupBy)),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private List<Map<String, Object>> sortRecords(List<Map<String, Object>> records, String sortBy, String order) {
        Comparator<Map<String, Object>> comparator = (r1, r2) -> {
            Object v1 = r1.get(sortBy);
            Object v2 = r2.get(sortBy);

            if (v1 == null && v2 == null) return 0;
            if (v1 == null) return -1;
            if (v2 == null) return 1;

            if (v1 instanceof Number && v2 instanceof Number) {
                return Double.compare(((Number) v1).doubleValue(), ((Number) v2).doubleValue());
            }

            return String.valueOf(v1).compareTo(String.valueOf(v2));
        };

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return records.stream()
                .filter(record -> record.containsKey(sortBy))
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}