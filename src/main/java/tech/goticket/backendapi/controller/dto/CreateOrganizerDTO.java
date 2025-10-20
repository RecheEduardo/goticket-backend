package tech.goticket.backendapi.controller.dto;

public record CreateOrganizerDTO(String email,
                                 String password,
                                 String organizerName,
                                 String legalName,
                                 String CNPJ    ) {
}
