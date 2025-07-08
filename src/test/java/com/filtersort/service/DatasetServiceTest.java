package com.filtersort.service;

import com.filtersort.dto.request.RecordInsertRequest;
import com.filtersort.dto.response.RecordInsertResponse;
import com.filtersort.entity.DatasetRecord;
import com.filtersort.repository.DatasetRecordRepository;
import com.filtersort.repository.DatasetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    @Mock
    private DatasetRepository datasetRepository;

    @Mock
    private DatasetRecordRepository recordRepository;

    @Mock
    private QuerySpecificationService specificationService;

    @InjectMocks
    private DatasetService datasetService;

    @Test
    void insertRecord_shouldCreateNewDatasetIfNotExists() {
        String datasetName = "TestData";
        RecordInsertRequest request = new RecordInsertRequest(Map.of("name", "John", "age", 30));

        when(datasetRepository.findByName(datasetName)).thenReturn(Optional.empty());
        when(datasetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(recordRepository.save(any())).thenAnswer(inv -> {
            DatasetRecord r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        RecordInsertResponse response = datasetService.insertRecord(datasetName, request);

        assertEquals("Record added successfully", response.getMessage());
        assertEquals(datasetName, response.getDataset());
        assertNotNull(response.getRecordId());

    }
}
