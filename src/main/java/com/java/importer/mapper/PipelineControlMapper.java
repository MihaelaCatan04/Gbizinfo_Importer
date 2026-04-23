package com.java.importer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface PipelineControlMapper {

    int tryStartImport(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("leaseSeconds") int leaseSeconds);

    int markImportSuccess(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("transactionDate") LocalDate transactionDate);

    int markImportFailure(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId);

    int renewImportLease(@Param("pipelineName") String pipelineName, @Param("nodeId") String nodeId, @Param("leaseSeconds") int leaseSeconds);

    LocalDate getExportDate(@Param("pipelineName") String pipelineName);

    int finishExportIfComplete(@Param("pipelineName") String pipelineName, @Param("transactionDate") LocalDate transactionDate);
}