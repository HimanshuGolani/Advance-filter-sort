package com.filtersort.repository;


import com.filtersort.entity.DatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long>,
        JpaSpecificationExecutor<DatasetRecord> {

    List<DatasetRecord> findByDatasetName(String datasetName);

    @Query("SELECT dr FROM DatasetRecord dr WHERE dr.dataset.name = :datasetName")
    List<DatasetRecord> findRecordsByDatasetName(@Param("datasetName") String datasetName);
}