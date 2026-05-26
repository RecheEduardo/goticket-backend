package tech.goticket.backendapi.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.admin.AdminRepository;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.ticket.Ticket;
import tech.goticket.backendapi.ticket.dto.TicketResponse;
import tech.goticket.backendapi.ticket.repository.TicketRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public TicketResponse findByIdForUser(UUID ticketId, UUID requesterId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket não encontrado"));

        if (!canSeeTicket(ticket, requesterId)) {
            throw new ResourceNotFoundException("Ticket não encontrado");
        }
        return TicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findByOrderIdForUser(Long orderId, UUID requesterId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada"));

        boolean isOwner = order.getBuyer().getUserId().equals(requesterId);
        boolean isAdmin = adminRepository.existsById(requesterId);
        if (!isOwner && !isAdmin) {
            throw new ResourceNotFoundException("Order não encontrada");
        }

        return ticketRepository.findByOrderId(orderId).stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findMyTickets(UUID buyerId) {
        return ticketRepository.findByBuyer_UserIdOrderByRegisterDateDesc(buyerId).stream()
                .map(TicketResponse::from)
                .toList();
    }

    private boolean canSeeTicket(Ticket ticket, UUID requesterId) {
        if (ticket.getBuyer().getUserId().equals(requesterId)) return true;
        return adminRepository.existsById(requesterId);
    }
}
