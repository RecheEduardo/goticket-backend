package tech.goticket.backendapi.order.dto;

import jakarta.validation.constraints.NotNull;

public record QuoteItemRequest(
        @NotNull Long batchAllotmentId,
        @NotNull Long ticketTypeId
) { }
