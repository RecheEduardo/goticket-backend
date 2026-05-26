package tech.goticket.backendapi.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record MyOrderListItemDTO(
        Long orderId,
        String status,
        Long eventId,
        String eventTitle,
        String eventImageS3Key,
        LocalDateTime eventStartDate,
        String venueName,
        String venueCity,
        Long itemCount,
        BigDecimal totalPrice,
        String currency,
        Instant placedAt,
        Instant expiresAt,
        Instant paidAt,
        Instant canceledAt
) { }