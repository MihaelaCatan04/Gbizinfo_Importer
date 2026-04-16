package com.java.importer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompatibilityOfChildcareAndWorkDto {
    private String corporateNumber;
    private String mergeKey;
    private Integer numberOfPaternityLeave;
    private Integer numberOfMaternityLeave;
    private Integer paternityLeaveAcquisitionNum;
    private Integer maternityLeaveAcquisitionNum;
}