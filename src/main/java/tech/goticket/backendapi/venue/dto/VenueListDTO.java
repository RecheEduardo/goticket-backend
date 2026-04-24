package tech.goticket.backendapi.venue.dto;

import java.util.List;

public record VenueListDTO(int page,
                           int pageSize,
                           int totalPages,
                           long totalElements,
                           List<VenueMinDTO> venueMinDTOList) {
}
