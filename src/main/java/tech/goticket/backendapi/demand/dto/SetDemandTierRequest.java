package tech.goticket.backendapi.demand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SetDemandTierRequest(
        @NotBlank @Pattern(regexp = "HIGH|NORMAL") String  tier,
        Integer validForMinutes
) {
}
