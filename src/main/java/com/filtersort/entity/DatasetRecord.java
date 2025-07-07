package com.filtersort.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "dataset_records")
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json_data", columnDefinition = "TEXT")
    private Map<String, Object> jsonData;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public DatasetRecord() {}

    public DatasetRecord(Dataset dataset, Map<String, Object> jsonData) {
        this.dataset = dataset;
        this.jsonData = jsonData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Dataset getDataset() { return dataset; }
    public void setDataset(Dataset dataset) { this.dataset = dataset; }

    public Map<String, Object> getJsonData() { return jsonData; }
    public void setJsonData(Map<String, Object> jsonData) { this.jsonData = jsonData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}