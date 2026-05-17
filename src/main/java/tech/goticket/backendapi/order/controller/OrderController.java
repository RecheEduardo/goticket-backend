package tech.goticket.backendapi.order.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.dto.*;
import tech.goticket.backendapi.order.service.OrderService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication) {
        UUID buyerId = UUID.fromString(authentication.getName());
        PlaceOrderResponse response = orderService.placeOrder(request, buyerId, idempotencyKey);

        return ResponseEntity.created(URI.create("/orders/" + response.orderId()))
                .body(response);
    }

    @PostMapping("/quote")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<QuoteResponse> quoteOrder(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(orderService.quote(request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());
        Order order = orderService.getById(orderId, requesterId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
