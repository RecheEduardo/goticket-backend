package tech.goticket.backendapi.controller.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record UserListDTO(int page,
                          int pageSize,
                          int totalPages,
                          long totalElements,
                          List<UserDTO> userDTOList) {
}
