package com.filtersort.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filtersort.dto.request.RecordInsertRequest;
import com.filtersort.dto.response.RecordInsertResponse;
import com.filtersort.service.DatasetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetController.class)
class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetService datasetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insertRecord_shouldReturn201AndResponse() throws Exception {
        RecordInsertRequest request = new RecordInsertRequest(Map.of("key", "value"));
        RecordInsertResponse response = new RecordInsertResponse("Success", "testDataset", 1L);

        when(datasetService.insertRecord(eq("testDataset"), any())).thenReturn(response);

        mockMvc.perform(post("/api/dataset/testDataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.dataset").value("testDataset"))
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void insertRecord_shouldReturn400WhenRequestBodyMissing() throws Exception {
        mockMvc.perform(post("/api/dataset/testDataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void queryDataset_shouldReturn200WhenNoParams() throws Exception {
        // You should mock your QueryResponse properly here
        mockMvc.perform(get("/api/dataset/testDataset/query"))
                .andExpect(status().isOk());
    }
}
