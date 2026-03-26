package com.java.importer.model.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static com.java.importer.util.HashUtil.*;

@Setter
@Getter
public class CompatibilityOfChildcareAndWork {
    @JsonProperty("number_of_paternity_leave")
    private Integer numberOfPaternityLeave;

    @JsonProperty("number_of_maternity_leave")
    private Integer numberOfMaternityLeave;

    @JsonProperty("paternity_leave_acquisition_num")
    private Integer paternityLeaveAcquisitionNum;

    @JsonProperty("maternity_leave_acquisition_num")
    private Integer maternityLeaveAcquisitionNum;

    public String compatChildcareMergeKey() {
        return mergeKeyOrNull(normInt(this.numberOfPaternityLeave), normInt(this.numberOfMaternityLeave), normInt(this.paternityLeaveAcquisitionNum), normInt(this.maternityLeaveAcquisitionNum));
    }
}
