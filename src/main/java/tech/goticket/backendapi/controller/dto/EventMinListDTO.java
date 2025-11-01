package tech.goticket.backendapi.controller.dto;

import java.util.List;

public record EventMinListDTO(int page,
                              int pageSize,
                              int totalPages,
                              long totalElements,
                              List<EventMinDTO> eventMinDTOList) {
}
