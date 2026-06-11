package tech.goticket.backendapi.order.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.dto.*;
import tech.goticket.backendapi.order.service.OrderService;
import tech.goticket.backendapi.ticket.dto.TicketResponse;
import tech.goticket.backendapi.ticket.service.TicketService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-Queue-Token", required = false) String queueToken,
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) throws IOException {
        UUID buyerId = UUID.fromString(authentication.getName());
        String rawBody = objectMapper.writeValueAsString(request);
        PlaceOrderResponse response = orderService.placeOrder(request, buyerId, idempotencyKey, rawBody, queueToken);

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

    @GetMapping("/{orderId}/tickets")
    @PreAuthorize("hasAnyAuthority('SCOPE_CLIENT', 'SCOPE_ADMIN')")
    public ResponseEntity<List<TicketResponse>> getTicketsByOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ticketService.findByOrderIdForUser(orderId, requesterId));
    }

    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<OrderStatusDTO> getOrderStatus(@PathVariable Long orderId,
                                                         Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        OrderStatusDTO dto = orderService.getStatus(orderId, requesterId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<MyOrderListDTO> listMyOrders(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int pageSize,
                                                       Authentication authentication) {
        UUID buyerId = UUID.fromString(authentication.getName());
        MyOrderListDTO orders = orderService.listMyOrders(buyerId, PageRequest.of(page, pageSize));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/summary")
    @PreAuthorize("hasAnyAuthority('SCOPE_CLIENT', 'SCOPE_ADMIN')")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable Long orderId,
                                                                Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(orderService.getSummary(orderId, requesterId));
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
