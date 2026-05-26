package tech.goticket.backendapi.order.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.dto.*;
import tech.goticket.backendapi.order.service.OrderService;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) throws IOException {
        UUID buyerId = UUID.fromString(authentication.getName());
        String rawBody = objectMapper.writeValueAsString(request);
        PlaceOrderResponse response = orderService.placeOrder(request, buyerId, idempotencyKey, rawBody);

        return ResponseEntity.created(URI.create("/orders/" + response.orderId()))
                .body(response);
    }

    @PostMapping("/quote")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<QuoteResponse> quoteOrder(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(orderService.quote(request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_CLIENT', 'SCOPE_ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());
        Order order = orderService.getById(orderId, requesterId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<Page<OrderListItemDTO>> listMyOrders(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int pageSize,
                                                         Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        Page<OrderListItemDTO> orders = orderService.listOrdersOfBuyer(
                requesterId,
                PageRequest.of(page, pageSize, Sort.by("placedAt").descending()));
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                                     @Valid @RequestBody(required = false) CancelOrderRequest body,
                                                     Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        String reason = (body == null || body.reason() == null) ? "Cancelado pelo cliente" : body.reason();
        Order order = orderService.cancelByBuyer(orderId, requesterId, reason);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
