package tech.goticket.backendapi.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.goticket.backendapi.event.*;
import tech.goticket.backendapi.event.dto.*;
import tech.goticket.backendapi.event.enums.EventStatus;
import tech.goticket.backendapi.event.enums.EventVisibility;
import tech.goticket.backendapi.event.repository.*;
import tech.goticket.backendapi.event.view.EventMinDetailsView;
import tech.goticket.backendapi.event.view.specifications.EventMinDetailsSpecifications;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.organizer.OrganizerService;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.shared.storage.FileStorageService;
import tech.goticket.backendapi.shared.storage.FileUpload;
import tech.goticket.backendapi.venue.Venue;
import tech.goticket.backendapi.venue.VenueRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventAuthorizationService eventAuthorizationService;

    private final EventMinDetailsRepository eventMinDetailsRepository;

    private final EventStatusRepository eventStatusRepository;

    private final EventVisibilityRepository eventVisibilityRepository;

    private final EventCategoryRepository eventCategoryRepository;

    private final EventImageRepository eventImageRepository;

    private final EventDateService eventDateService;

    private final VenueRepository venueRepository;

    private final OrganizerService organizerService;

    private final FileStorageService fileStorageService;

    private final ObjectMapper objectMapper;

    @Transactional
    public EventPageDTO findByEventID(Long eventID, UUID userId) {
        Event event = eventRepository.findByEventId(eventID)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        boolean isEventVisibleAndApproved = event.getEventVisibility().getVisibilityId() == EventVisibility.Values.PUBLIC.getVisibilityId()
                && (event.getStatus().isApproved() || event.getStatus().isPostponed());
        boolean isVenueActiveAndApproved = event.getVenue().getApprovalDate() != null
                && event.getVenue().getStatus().getStatusId() == Status.Values.ACTIVE.getStatusId();
        boolean isOrganizerActive = event.getOrganizer().getStatus().getStatusId() == Status.Values.ACTIVE.getStatusId();

        if (!isEventVisibleAndApproved
                || !isVenueActiveAndApproved
                || !isOrganizerActive) {
            eventAuthorizationService.requireOwnerOrAdmin(event, userId, "Usuário não tem permissão para visualizar o evento solicitado.");
        }

        String venueMapUrl = fileStorageService.resolvePublicUrl(event.getVenue().getSectorMapS3Key());
        return new EventPageDTO(event, venueMapUrl);
    }

    @Transactional
    public EventFullDTO findByEventIDWithFullInfo(Long eventID, UUID userId) {
        Event event = eventRepository.findByEventId(eventID)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        eventAuthorizationService.requireOwnerOrAdmin(event, userId,
                "Usuário não tem permissão para visualizar o evento com todos os detalhes.");

        String venueMapUrl = fileStorageService.resolvePublicUrl(
                event.getVenue().getSectorMapS3Key()
        );

        return new EventFullDTO(event, venueMapUrl);
    }

    @Transactional
    public EventMinListDTO findApprovedPublicEvents(String title,
                                                    Long categoryId,
                                                    Double startingPrice,
                                                    String venueState,
                                                    String venueCity,
                                                    PageRequest pageRequest) {

        Specification<EventMinDetailsView> spec = Specification.allOf(
                EventMinDetailsSpecifications.hasTitle(title),
                EventMinDetailsSpecifications.hasCategory(categoryId),
                EventMinDetailsSpecifications.hasStartingPrice(startingPrice),
                EventMinDetailsSpecifications.hasVenueState(venueState),
                EventMinDetailsSpecifications.hasVenueCity(venueCity));

        var events = eventMinDetailsRepository.findAll(spec, pageRequest)
                .map(EventMinDTO::new);

        return new EventMinListDTO(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                events.getTotalPages(),
                events.getTotalElements(),
                events.toList());
    }

    @Transactional
    public Event createEvent(CreateEventDTO dto, UUID requestUserId, boolean isAdmin) {
        UUID targetOrganizerId;
        if (isAdmin) {
            if (dto.organizerID() == null) {
                throw new InvalidArgumentException(
                        "O ID do organizador é obrigatório quando a criação é feita por um Administrador."
                );
            }
            targetOrganizerId = dto.organizerID();
        } else {
            targetOrganizerId = requestUserId;
        }

        Organizer organizer = organizerService.findById(targetOrganizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador informado não encontrado."));

        EventStatus approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());
        EventVisibility privateVisibility = eventVisibilityRepository.findByName(EventVisibility.Values.PRIVATE.name())
                .orElseThrow(() -> new ResourceNotFoundException("Visibilidade de evento não encontrada."));

        EventCategory requestedCategory = eventCategoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de evento não encontrada."));

        Venue requestedVenue = venueRepository.findById(dto.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Espaço informado não encontrado."));

        Instant now = Instant.now();

        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setAgeRestriction(dto.ageRestriction());
        event.setEventVisibility(privateVisibility);
        event.setCategory(requestedCategory);
        event.setRegisterDate(now);
        event.setLastUpdateDate(now);
        event.setStatus(approvedStatus);
        event.setVenue(requestedVenue);
        event.setOrganizer(organizer);

        if (dto.salesStartDate() != null) {
            event.setSalesStartDate(dto.salesStartDate());
        }

        for (EventDateInputDTO dateInput : dto.eventDates()) {
            eventDateService.attachEventDate(event, dateInput.startDate(), dateInput.endDate());
        }

        event.recalculateDateRange();

        return eventRepository.save(event);
    }

    private static final Set<String> PATCHABLE_FIELDS = Set.of(
            "title",
            "description",
            "ageRestriction",
            "salesStartDate",
            "category"
    );

    @Transactional
    public EventFullDTO updateEvent(Long eventId, JsonNode patchNode, UUID userId) {
        Set<String> attempted = new HashSet<>();
        patchNode.fieldNames().forEachRemaining(attempted::add);

        Set<String> notAllowed = new HashSet<>(attempted);
        notAllowed.removeAll(PATCHABLE_FIELDS);

        if (!notAllowed.isEmpty()) {
            throw new InvalidArgumentException(
                    "Os seguintes campos não podem ser editados por este endpoint: " + notAllowed
            );
        }

        Event existingEvent = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        eventAuthorizationService.requireOwnerOrAdmin(existingEvent, userId, "Usuário não tem permissão para executar esta ação.");

        try {
            objectMapper.readerForUpdating(existingEvent).readValue(patchNode);
            existingEvent.setLastUpdateDate(Instant.now());
            Event saved = eventRepository.save(existingEvent);

            String venueMapUrl = fileStorageService.resolvePublicUrl(
                    saved.getVenue().getSectorMapS3Key()
            );

            return new EventFullDTO(saved, venueMapUrl);
        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar evento.");
        }
    }

    @Transactional
    public void updateVisibility(Long eventId, EventVisibility.Values visibilityValue, UUID userId) {
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        eventAuthorizationService.requireOwnerOrAdmin(event, userId, "Usuário não tem permissão para executar esta ação.");

        if (event.getEventVisibility().getName().equals(visibilityValue.name())) {
            throw new InvalidArgumentException("O evento já possui a visibilidade informada.");
        }

        if (!event.getStatus().isApproved()) {
            throw new ForbiddenActionException("Não é permitido alterar a visibilidade de um evento com status: " + event.getStatus().getName());
        }

        EventVisibility visibility = eventVisibilityRepository.findByName(visibilityValue.name())
                .orElseThrow(() -> new InvalidArgumentException("Configuração de visibilidade não encontrada no sistema."));

        event.setEventVisibility(visibility);
        eventRepository.save(event);
    }

    @Transactional
    public void replaceImages(Long eventId,
                              List<EventImageOrderItemDTO> metadata,
                              List<MultipartFile> newImages,
                              UUID userId) {

        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        eventAuthorizationService.requireOwnerOrAdmin(event, userId, "Usuário não tem permissão para executar esta ação.");

        if (metadata == null || metadata.isEmpty()) {
            throw new InvalidArgumentException("A lista de imagens não pode ser vazia.");
        }

        long newCount = metadata.stream().filter(m -> "new".equals(m.type())).count();
        int actualNewFiles = (newImages != null) ? newImages.size() : 0;

        if (newCount != actualNewFiles) {
            throw new InvalidArgumentException(
                    "O metadata referencia " + newCount +
                            " imagem(ns) nova(s), mas foram enviado(s) " + actualNewFiles + " arquivo(s)."
            );
        }

        Map<String, EventImage> existingByKey = event.getImages().stream()
                .collect(Collectors.toMap(EventImage::getS3Key, img -> img));

        Set<String> keysToKeep = metadata.stream()
                .filter(m -> "existing".equals(m.type()))
                .map(EventImageOrderItemDTO::s3Key)
                .collect(Collectors.toSet());

        List<EventImage> toRemove = event.getImages().stream()
                .filter(img -> !keysToKeep.contains(img.getS3Key()))
                .toList();

        for (EventImage img : toRemove) {
            fileStorageService.delete(img.getS3Key());
            event.getImages().remove(img);
        }

        List<EventImage> updatedImages = new ArrayList<>();

        for (int i = 0; i < metadata.size(); i++) {
            EventImageOrderItemDTO item = metadata.get(i);

            if ("existing".equals(item.type())) {
                EventImage existing = existingByKey.get(item.s3Key());
                if (existing == null) {
                    throw new InvalidArgumentException(
                            "Imagem com s3Key '" + item.s3Key() + "' não pertence a este evento."
                    );
                }
                existing.setOrdination(i);
                updatedImages.add(existing);

            } else if ("new".equals(item.type())) {
                MultipartFile file = newImages.get(item.fileIndex());
                String key = fileStorageService.upload(new FileUpload(file));

                EventImage newImage = new EventImage();
                newImage.setS3Key(key);
                newImage.setOrdination(i);
                newImage.setEvent(event);
                updatedImages.add(newImage);

            } else {
                throw new InvalidArgumentException("Tipo de item inválido: " + item.type());
            }
        }

        event.getImages().clear();
        event.getImages().addAll(updatedImages);

        eventRepository.save(event);
    }

    @Transactional
    public void deleteEventById(Long eventId, UUID userId) {
        Event existingEvent = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new InvalidArgumentException("Evento não encontrado"));

        eventAuthorizationService.requireOwnerOrAdmin(existingEvent, userId, "Usuário não tem permissão para executar esta ação.");

        eventRepository.delete(existingEvent);
    }

    @Transactional
    public void deleteEventImageByKey(Long eventId, String imageKey, UUID userId) {
        Event existingEvent = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new InvalidArgumentException("Evento não encontrado"));

        eventAuthorizationService.requireOwnerOrAdmin(existingEvent, userId, "Usuário não tem permissão para executar esta ação.");

        var eventImages = eventImageRepository.findByEvent_EventId(eventId);

        EventImage removedImage = eventImages.stream()
                .filter(i -> i.getS3Key().equals(imageKey))
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Key de imagem não encontrada neste evento."));

        eventImageRepository.delete(removedImage);
    }
}
