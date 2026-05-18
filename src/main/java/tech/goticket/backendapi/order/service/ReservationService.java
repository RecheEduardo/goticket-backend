package tech.goticket.backendapi.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.SoldOutException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.repository.BatchAllotmentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final BatchAllotmentRepository allotmentRepository;

    public BatchAllotment reserveOrThrow(Long allotmentId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade informada deve ser positiva. Recebido: " + quantity);
        }

        BatchAllotment allotment = allotmentRepository.findById(allotmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Allotment não foi encontrado: " + allotmentId));

        int available = allotment.getAvailableTickets();
        if (available < quantity) {
            throw new SoldOutException(
                    "Estoque insuficiente no lote/tipo selecionado. " +
                            "Disponível: " + available + ", solicitado: " + quantity);
        }

        allotment.setReservedTickets(allotment.getReservedTickets() + quantity);
        BatchAllotment saved = allotmentRepository.save(allotment);

        log.debug("Reservados {} tickets no allotment {}. Disponível agora: {}",
                quantity, allotmentId, saved.getAvailableTickets());

        return saved;
    }

    public BatchAllotment releaseReservation(Long allotmentId, int quantity) {
        BatchAllotment allotment = allotmentRepository.findById(allotmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Allotment não encontrado: " + allotmentId));

        int newReserved = Math.max(0, allotment.getReservedTickets() - quantity);
        allotment.setReservedTickets(newReserved);
        return allotmentRepository.save(allotment);
    }

    public BatchAllotment confirmSale(Long allotmentId, int quantity) {
        BatchAllotment allotment = allotmentRepository.findById(allotmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Allotment não encontrado: " + allotmentId));

        if (allotment.getReservedTickets() < quantity) {
            throw new IllegalStateException(
                    "Allotment " + allotmentId + " tem reserved=" +
                            allotment.getReservedTickets() + " mas confirmação pede " + quantity);
        }

        allotment.setReservedTickets(Math.max(0, allotment.getReservedTickets() - quantity));
        allotment.setSoldTickets(allotment.getSoldTickets() + quantity);
        return allotmentRepository.save(allotment);
    }
}
