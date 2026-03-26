package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ManagementIndexDto {
    private String runId;
    private String financeMergeKey;
    private String mergeKey;
    private String period;
    private Long netSalesSummaryOfBusinessResults;
    private String netSalesSummaryOfBusinessResultsUnitRef;
    private Long operatingRevenue1SummaryOfBusinessResults;
    private String operatingRevenue1SummaryOfBusinessResultsUnitRef;
    private Long operatingRevenue2SummaryOfBusinessResults;
    private String operatingRevenue2SummaryOfBusinessResultsUnitRef;
    private Long grossOperatingRevenueSummaryOfBusinessResults;
    private String grossOperatingRevenueSummaryOfBusinessResultsUnitRef;
    private Long ordinaryIncomeSummaryOfBusinessResults;
    private String ordinaryIncomeSummaryOfBusinessResultsUnitRef;
    private Long netPremiumsWrittenSummaryOfBusinessResultIns;
    private String netPremiumsWrittenSummaryOfBusinessResultsInsUnitRef;
    private Long ordinaryIncomeLossSummaryOfBusinessResults;
    private String ordinaryIncomeLossSummaryOfBusinessResultsUnitRef;
    private Long netIncomeLossSummaryOfBusinessResults;
    private String netIncomeLossSummaryOfBusinessResultsUnitRef;
    private Long capitalStockSummaryOfBusinessResults;
    private String capitalStockSummaryOfBusinessResultsUnitRef;
    private Long netAssetsSummaryOfBusinessResults;
    private String netAssetsSummaryOfBusinessResultsUnitRef;
    private Long totalAssetsSummaryOfBusinessResults;
    private String totalAssetsSummaryOfBusinessResultsUnitRef;
    private Long numberOfEmployees;
    private String numberOfEmployeesUnitRef;
}