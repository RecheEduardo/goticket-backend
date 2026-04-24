package tech.goticket.backendapi.client.dto;

import java.util.List;

public record ClientListDTO(int page,
                            int pageSize,
                            int totalPages,
                            long totalElements,
                            List<ClientMinDTO> clientMinDTOList) {
}
