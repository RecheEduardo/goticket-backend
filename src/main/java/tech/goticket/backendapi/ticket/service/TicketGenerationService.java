package tech.goticket.backendapi.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.client.Client;
import tech.goticket.backendapi.order.OrderItem;
import tech.goticket.backendapi.ticket.Ticket;
import tech.goticket.backendapi.ticket.enums.TicketStatus;
import tech.goticket.backendapi.ticket.repository.TicketRepository;
import tech.goticket.backendapi.ticket.repository.TicketStatusRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketGenerationService {
    private final TicketRepository ticketRepository;
    private final TicketStatusRepository ticketStatusRepository;

    public Ticket generateTicket(OrderItem item, Client buyer) {
        TicketStatus active = ticketStatusRepository.findByName(TicketStatus.Values.ACTIVE.name())
                .orElseThrow(() -> new IllegalStateException("TicketStatus ACTIVE não existente."));

        Ticket ticket = new Ticket();
        ticket.setAllotment(item.getBatchAllotment());
        ticket.setPaidPrice(item.getUnitPrice());
        ticket.setFeesPaid(item.getFeeAmount());
        ticket.setBuyer(buyer);
        ticket.setHolderName(item.getHolderName());
        ticket.setHolderDocument(item.getHolderDocument());
        ticket.setEligibilityType(item.getEligibilityType());
        ticket.setEligibilityDocumentNumber(item.getEligibilityDocumentNumber());
        ticket.setStatus(active);
        ticket.setRegisterDate(Instant.now());
        ticket.setQrToken(generateQrToken());

        return ticketRepository.save(ticket);
    }

    public void markRefunded(Ticket ticket) {
        TicketStatus refunded = ticketStatusRepository.findByName(TicketStatus.Values.REFUNDED.name())
                .orElseThrow(() -> new IllegalStateException("TicketStatus REFUNDED não existente."));
        ticket.setStatus(refunded);
        ticketRepository.save(ticket);
    }

    private String generateQrToken() {
        return (UUID.randomUUID().toString() + UUID.randomUUID().toString())
                .replace("-","")
                .substring(0,64);
    }
}
