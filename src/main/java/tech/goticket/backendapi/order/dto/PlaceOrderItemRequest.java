package tech.goticket.backendapi.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlaceOrderItemRequest(
        @NotNull Long batchAllotmentId,
        @NotNull Long ticketTypeId,
        @NotBlank @Size(max = 200) String holderName,
        @NotBlank @Size(min = 11, max = 20) String holderDocument,
        Long eligibilityTypeId,
        @Size(max = 50) String eligibilityDocumentNumber
) { }

