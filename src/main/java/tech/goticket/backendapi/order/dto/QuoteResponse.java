package tech.goticket.backendapi.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record QuoteResponse(
        Long eventId,
        String eventTitle,
        Long eventDateId,
        String currency,
        List<QuoteItemResponse> items,
        BigDecimal subtotal,
        BigDecimal feesTotal,
        BigDecimal totalPrice
) {
}
