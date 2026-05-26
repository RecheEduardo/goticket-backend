package tech.goticket.backendapi.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.EventDateSector;
import tech.goticket.backendapi.event.repository.EventDateSectorRepository;
import tech.goticket.backendapi.event.service.EventAuthorizationService;
import tech.goticket.backendapi.shared.exception.ConflictException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.ticket.dto.CreateBatchAllotmentDTO;
import tech.goticket.backendapi.ticket.dto.CreateTicketBatchDTO;
import tech.goticket.backendapi.ticket.enums.TicketType;
import tech.goticket.backendapi.ticket.repository.TicketBatchRepository;
import tech.goticket.backendapi.ticket.repository.TicketTypeRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketBatchService {

    private final EventDateSectorRepository eventDateSectorRepository;
    private final TicketBatchRepository ticketBatchRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final EventAuthorizationService eventAuthorizationService;
    private final BatchAllotmentValidator batchAllotmentValidator;

    @Transactional
    public TicketBatch createBatch(Long eventId, Long eventDateSectorId,
                                   CreateTicketBatchDTO dto, UUID userId) {
        EventDateSector eds = eventDateSectorRepository.findById(eventDateSectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Setor-data não encontrado."));

        EventDate eventDate = eds.getEventDate();
        Event event = eventDate.getEvent();

        if (!event.getEventId().equals(eventId)) {
            throw new ResourceNotFoundException("Setor-data não pertence a este evento.");
        }

        eventAuthorizationService.requireOwnerOrAdmin(event, userId,
                "Usuário não tem permissão para alterar este evento.");

        int nextBatchNumber = ticketBatchRepository.findMaxBatchNumber(eventDateSectorId) + 1;
        int totalTickets = dto.allotments().stream().mapToInt(CreateBatchAllotmentDTO::quota).sum();

        TicketBatch batch = new TicketBatch();
        batch.setBatchNumber(nextBatchNumber);
        batch.setPrice(dto.price());
        batch.setActivationDate(dto.activationDate());
        batch.setTotalTickets(totalTickets);
        batch.setEventDateSector(eds);

        List<BatchAllotment> allotments = buildAllotments(dto.allotments(), batch);
        batch.setAllotments(allotments);

        for (BatchAllotment a : allotments) {
            batchAllotmentValidator.validateAllotment(a);
        }

        batchAllotmentValidator.validateSectorHalfQuotaCompliance(eds);

        return ticketBatchRepository.save(batch);
    }

    @Transactional
    public void deleteBatch(Long eventId, Long batchId, UUID userId) {
        TicketBatch batch = ticketBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado."));

        EventDateSector eds = batch.getEventDateSector();
        Event event = eds.getEventDate().getEvent();

        if (!event.getEventId().equals(eventId)) {
            throw new ResourceNotFoundException("Lote não pertence a este evento.");
        }

        eventAuthorizationService.requireOwnerOrAdmin(event, userId,
                "Usuário não tem permissão para alterar este evento.");

        if (batch.getSoldTickets() != null && batch.getSoldTickets() > 0) {
            throw new ConflictException("Não é possível remover um lote com ingressos vendidos.");
        }

        eds.getBatches().remove(batch);
    }

    private List<BatchAllotment> buildAllotments(List<CreateBatchAllotmentDTO> dtos, TicketBatch batch) {
        List<BatchAllotment> allotments = new ArrayList<>();
        for (CreateBatchAllotmentDTO dto : dtos) {
            TicketType type = ticketTypeRepository.findById(dto.ticketTypeId())
                    .orElseThrow(() -> new InvalidArgumentException(
                            "Tipo de ingresso não encontrado: " + dto.ticketTypeId()));

            Instant now = Instant.now();
            BatchAllotment allotment = new BatchAllotment();
            allotment.setTicketType(type);
            allotment.setQuota(dto.quota());
            allotment.setPrice(dto.price());
            allotment.setSoldTickets(0);
            allotment.setReservedTickets(0);
            allotment.setRegisterDate(now);
            allotment.setLastUpdateDate(now);
            allotment.setBatch(batch);

            allotments.add(allotment);
        }
        return allotments;
    }
}
