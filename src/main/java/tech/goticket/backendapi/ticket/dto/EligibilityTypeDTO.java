package tech.goticket.backendapi.ticket.dto;

import tech.goticket.backendapi.ticket.enums.EligibilityType;

public record EligibilityTypeDTO(Long eligibilityTypeId, String name) {
    public static EligibilityTypeDTO from(EligibilityType e) {
        return new EligibilityTypeDTO(e.getEligibilityTypeId(), e.getName());
    }
}
