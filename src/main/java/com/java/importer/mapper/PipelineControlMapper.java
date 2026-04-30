package com.java.importer.mapper;

import com.java.importer.model.dto.PipelineControlDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PipelineControlMapper {

    PipelineControlDto findMain(@Param("pipelineName") String pipelineName);

    void insertInitial(@Param("pipelineName") String pipelineName);

    int tryStartImport(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("leaseSeconds") int leaseSeconds);

    int markImportSuccess(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("transactionDate") LocalDate transactionDate);

    int markImportFailure(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId);

    LocalDate getExportDate(@Param("pipelineName") String pipelineName);

    int finishExportIfComplete(@Param("pipelineName") String pipelineName, @Param("transactionDate") LocalDate transactionDate);

    int renewImportLease(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("leaseSeconds") int leaseSeconds);
}