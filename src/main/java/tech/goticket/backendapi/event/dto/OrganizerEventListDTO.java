package tech.goticket.backendapi.event.dto;

import java.util.List;

public record OrganizerEventListDTO(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        List<OrganizerEventListItemDTO> events
) {}
