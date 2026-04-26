package tech.goticket.backendapi.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventImage;
import tech.goticket.backendapi.event.EventVisibility;
import tech.goticket.backendapi.event.dto.EventMinDTO;
import tech.goticket.backendapi.event.dto.EventMinListDTO;
import tech.goticket.backendapi.event.repository.*;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.PatchProgressingException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.storage.FileStorageService;
import tech.goticket.backendapi.shared.storage.FileUpload;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMinDetailsRepository eventMinDetailsRepository;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    private EventVisibilityRepository eventVisibilityRepository;

    @Autowired
    private EventImageRepository eventImageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Event> findByEventID(Long eventID) { return eventRepository.findByEventID(eventID); }

    /*@Transactional
    public EventMinListDTO findApprovedPublicEvents(PageRequest pageRequest) {
        var approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());
        var publicStatus = eventVisibilityRepository.findByName(EventVisibility.Values.PUBLIC.name())
                .orElseThrow(() -> new ResourceNotFoundException("Visibilidade pública não encontrada."));
        var events = eventRepository.findAllEventsByStatusAndEventVisibility(approvedStatus, publicStatus, pageRequest)
                .map(EventMinDTO::new);

        return new EventMinListDTO(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                events.getTotalPages(),
                events.getTotalElements(),
                events.toList());
    }*/

    @Transactional
    public EventMinListDTO findApprovedPublicEvents(PageRequest pageRequest) {
        var events = eventMinDetailsRepository.findAll(pageRequest)
                .map(EventMinDTO::new);

        return new EventMinListDTO(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                events.getTotalPages(),
                events.getTotalElements(),
                events.toList());
    }

    @Transactional
    public EventMinListDTO findApprovedPublicEventsByCategory(Long categoryId, PageRequest pageRequest) {
        var events = eventMinDetailsRepository.findAllByCategoryId(categoryId, pageRequest)
                .map(EventMinDTO::new);

        return new EventMinListDTO(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                events.getTotalPages(),
                events.getTotalElements(),
                events.toList());
    }

    @Transactional
    public void saveEvent(Event event) { eventRepository.save(event); }

    @Transactional
    public Event updateEvent(Long eventId, JsonNode patchNode, UUID userId) {
        if (patchNode.has("eventVisibility")) {
            throw new InvalidArgumentException("A visibilidade do evento não pode ser editada por este endpoint.");
        }

        Event existingEvent = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        validateUserPermission(existingEvent, userId);

        try {
            objectMapper.readerForUpdating(existingEvent).readValue(patchNode);

            return eventRepository.save(existingEvent);
        } catch (Exception e) {
            throw new PatchProgressingException("Erro ao atualizar evento.");
        }
    }

    @Transactional
    public void updateVisibility(Long eventId, EventVisibility.Values visibilityValue, UUID userId) {
        Event event = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        validateUserPermission(event, userId);

        if (event.getEventVisibility().getName().equals(visibilityValue.name())) {
            throw new InvalidArgumentException("O evento já possui a visibilidade informada.");
        }

        if (!event.getStatus().isApproved()) {
            throw new ForbiddenActionException("Não é permitido alterar a visibilidade de um evento com status: " + event.getStatus().getName());
        }

        EventVisibility visibility = eventVisibilityRepository.findByName(visibilityValue.name())
                .orElseThrow(() -> { return new InvalidArgumentException("Configuração de visibilidade não encontrada no sistema."); });

        event.setEventVisibility(visibility);
        eventRepository.save(event);
    }

    @Transactional
    public void uploadImages(Long eventId, List<MultipartFile> images, int mainImageIndex, UUID userId) {
        Event event = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        validateUserPermission(event, userId);

        if (mainImageIndex < 0 || mainImageIndex >= images.size()) {
            throw new InvalidArgumentException("O índice da imagem principal é inválido.");
        }

        event.getImages().forEach(img -> img.setMainImage(false));

        for (int i = 0; i < images.size(); i++) {
            String key = fileStorageService.upload(new FileUpload(images.get(i)));

            EventImage eventImage = new EventImage();
            eventImage.setS3Key(key);
            eventImage.setMainImage(i == mainImageIndex);
            eventImage.setEvent(event);

            event.getImages().add(eventImage);
        }

        eventRepository.save(event);
    }

    @Transactional
    public void deleteEventById(Long eventId, UUID userId) {
        Event existingEvent = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new InvalidArgumentException("Evento não encontrado"));

        validateUserPermission(existingEvent, userId);

        eventRepository.delete(existingEvent);
    }

    @Transactional
    public void deleteEventImageByKey(Long eventId, String imageKey, UUID userId) {
        Event existingEvent = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new InvalidArgumentException("Evento não encontrado"));

        validateUserPermission(existingEvent, userId);

        var eventImages = eventImageRepository.findByEvent_EventID(eventId);

        EventImage removedImage = eventImages.stream()
                .filter(i -> i.getS3Key().equals(imageKey))
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Key de imagem não encontrada neste evento."));

        eventImageRepository.delete(removedImage);
    }

    // Auxiliar para lógica de permissão
    private void validateUserPermission(Event event, UUID userId) {
        User requestUser = userService.findById(userId)
                .orElseThrow(() -> new ForbiddenActionException("Um erro ocorreu na sessão atual, faça login novamente."));

        boolean isAdmin = requestUser.getRole().getName().equals(Role.Values.ADMIN.name());
        boolean isEventOwner = requestUser.getUserID().equals(event.getOrganizer().getUserID());

        if(!isAdmin && !isEventOwner) {
            throw new ForbiddenActionException("Usuário não tem permissão para executar esta ação.");
        }
    }
}
