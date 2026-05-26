package tech.goticket.backendapi.ticket.dto;

import tech.goticket.backendapi.ticket.Ticket;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID ticketId,
        String status,
        String qrToken,
        String holderName,
        String holderDocument,
        String ticketTypeName,
        String eligibilityTypeName,
        BigDecimal paidPrice,
        BigDecimal feesPaid,
        Instant registerDate,
        Instant usedDate,

        Long eventId,
        String eventTitle,
        LocalDateTime eventStartDate,
        LocalDateTime eventEndDate,
        String sectorName,
        String venueName,
        String venueCity,
        String venueState
) {
    public static TicketResponse from(Ticket t) {
        var batch = t.getAllotment().getBatch();
        var eds = batch.getEventDateSector();
        var eventDate = eds.getEventDate();
        var event = eventDate.getEvent();
        var sector = eds.getEventSector();
        var venue = event.getVenue();

        return new TicketResponse(
                t.getTicketId(),
                t.getStatus().getName(),
                t.getQrToken(),
                t.getHolderName(),
                t.getHolderDocument(),
                t.getAllotment().getTicketType().getName(),
                t.getEligibilityType() != null ? t.getEligibilityType().getName() : null,
                t.getPaidPrice(),
                t.getFeesPaid(),
                t.getRegisterDate(),
                t.getUsedDate(),
                event.getEventId(),
                event.getTitle(),
                eventDate.getStartDate(),
                eventDate.getEndDate(),
                sector.getName(),
                venue.getName(),
                venue.getCity(),
                venue.getState()
        );
    }
}
