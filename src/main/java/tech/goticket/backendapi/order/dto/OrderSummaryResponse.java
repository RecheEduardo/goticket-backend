package tech.goticket.backendapi.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
        Long orderId,
        String status,
        Long eventId,
        String eventTitle,
        String eventImageS3Key,
        LocalDateTime eventStartDate,
        String venueName,
        String venueCity,
        BigDecimal subtotal,
        BigDecimal feesTotal,
        BigDecimal totalPrice,
        String currency,
        Instant paidAt,
        List<OrderSummaryItemDTO> items
) { }