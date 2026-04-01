package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class ManagementIndex {
    @JsonProperty("period")
    private String period;

    @JsonProperty("net_sales_summary_of_business_results")
    private Long netSalesSummaryOfBusinessResults;

    @JsonProperty("net_sales_summary_of_business_results_unit_ref")
    private String netSalesSummaryOfBusinessResultsUnitRef;

    @JsonProperty("operating_revenue1_summary_of_business_results")
    private Long operatingRevenue1SummaryOfBusinessResults;

    @JsonProperty("operating_revenue1_summary_of_business_results_unit_ref")
    private String operatingRevenue1SummaryOfBusinessResultsUnitRef;

    @JsonProperty("operating_revenue2_summary_of_business_results")
    private Long operatingRevenue2SummaryOfBusinessResults;

    @JsonProperty("operating_revenue2_summary_of_business_results_unit_ref")
    private String operatingRevenue2SummaryOfBusinessResultsUnitRef;

    @JsonProperty("gross_operating_revenue_summary_of_business_results")
    private Long grossOperatingRevenueSummaryOfBusinessResults;

    @JsonProperty("gross_operating_revenue_summary_of_business_results_unit_ref")
    private String grossOperatingRevenueSummaryOfBusinessResultsUnitRef;

    @JsonProperty("ordinary_income_summary_of_business_results")
    private Long ordinaryIncomeSummaryOfBusinessResults;

    @JsonProperty("ordinary_income_summary_of_business_results_unit_ref")
    private String ordinaryIncomeSummaryOfBusinessResultsUnitRef;

    @JsonProperty("net_premiums_written_summary_of_business_results_ins")
    private Long netPremiumsWrittenSummaryOfBusinessResultIns;

    @JsonProperty("net_premiums_written_summary_of_business_results_ins_unit_ref")
    private String netPremiumsWrittenSummaryOfBusinessResultsInsUnitRef;

    @JsonProperty("ordinary_income_loss_summary_of_business_results")
    private Long ordinaryIncomeLossSummaryOfBusinessResults;

    @JsonProperty("ordinary_income_loss_summary_of_business_results_unit_ref")
    private String ordinaryIncomeLossSummaryOfBusinessResultsUnitRef;

    @JsonProperty("net_income_loss_summary_of_business_results")
    private Long netIncomeLossSummaryOfBusinessResults;

    @JsonProperty("net_income_loss_summary_of_business_results_unit_ref")
    private String netIncomeLossSummaryOfBusinessResultsUnitRef;

    @JsonProperty("capital_stock_summary_of_business_results")
    private Long capitalStockSummaryOfBusinessResults;

    @JsonProperty("capital_stock_summary_of_business_results_unit_ref")
    private String capitalStockSummaryOfBusinessResultsUnitRef;

    @JsonProperty("net_assets_summary_of_business_results")
    private Long netAssetsSummaryOfBusinessResults;

    @JsonProperty("net_assets_summary_of_business_results_unit_ref")
    private String netAssetsSummaryOfBusinessResultsUnitRef;

    @JsonProperty("total_assets_summary_of_business_results")
    private Long totalAssetsSummaryOfBusinessResults;

    @JsonProperty("total_assets_summary_of_business_results_unit_ref")
    private String totalAssetsSummaryOfBusinessResultsUnitRef;

    @JsonProperty("number_of_employees")
    private Long numberOfEmployees;

    @JsonProperty("number_of_employees_unit_ref")
    private String numberOfEmployeesUnitRef;

    public String managementIndexMergeKey() {
        return mergeKeyOrNull(normText(this.period), normLong(this.netSalesSummaryOfBusinessResults), normText(this.netSalesSummaryOfBusinessResultsUnitRef), normLong(this.operatingRevenue1SummaryOfBusinessResults), normText(this.operatingRevenue1SummaryOfBusinessResultsUnitRef), normLong(this.operatingRevenue2SummaryOfBusinessResults), normText(this.operatingRevenue1SummaryOfBusinessResultsUnitRef), normLong(this.grossOperatingRevenueSummaryOfBusinessResults), normText(this.grossOperatingRevenueSummaryOfBusinessResultsUnitRef), normLong(this.ordinaryIncomeSummaryOfBusinessResults), normText(this.ordinaryIncomeSummaryOfBusinessResultsUnitRef), normLong(this.netPremiumsWrittenSummaryOfBusinessResultIns), normText(this.netPremiumsWrittenSummaryOfBusinessResultsInsUnitRef), normLong(this.ordinaryIncomeLossSummaryOfBusinessResults), normText(this.ordinaryIncomeSummaryOfBusinessResultsUnitRef), normLong(this.netIncomeLossSummaryOfBusinessResults), normText(this.netIncomeLossSummaryOfBusinessResultsUnitRef), normLong(this.capitalStockSummaryOfBusinessResults), normText(this.capitalStockSummaryOfBusinessResultsUnitRef), normLong(this.netAssetsSummaryOfBusinessResults), normText(this.netAssetsSummaryOfBusinessResultsUnitRef), normLong(this.totalAssetsSummaryOfBusinessResults), normText(this.totalAssetsSummaryOfBusinessResultsUnitRef), normLong(this.numberOfEmployees), normText(this.numberOfEmployeesUnitRef));
    }
}
