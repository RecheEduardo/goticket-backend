package tech.goticket.backendapi.user.dto;

import java.util.List;

public record UserListDTO(int page,
                          int pageSize,
                          int totalPages,
                          long totalElements,
                          List<UserDTO> userDTOList) {
}
