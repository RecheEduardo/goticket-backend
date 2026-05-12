package tech.goticket.backendapi.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull Long eventDateId,
        @NotEmpty @Size(max = 10) @Valid List<PlaceOrderItemRequest> items
) { }