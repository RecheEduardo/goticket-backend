package tech.goticket.backendapi.controller.dto;

import java.time.LocalDate;

public record CreateClientDTO(String email,
                              String password,
                              String fullName,
                              Integer sex,
                              String identityDocument,
                              String birthDate) {
}
