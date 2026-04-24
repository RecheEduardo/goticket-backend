package tech.goticket.backendapi.organizer.dto;

import java.util.List;

public record OrganizerListDTO(int page,
                               int pageSize,
                               int totalPages,
                               long totalElements,
                               List<OrganizerMinDTO> organizerMinDTOList) {
}
