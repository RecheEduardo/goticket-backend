package tech.goticket.backendapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOrganizerDTO(
        @NotBlank(message = "O e-mail é um campo obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "A senha é um campo obrigatório.")
        String password,

        @NotBlank(message = "O nome fantasia do organizador é um campo obrigatório.")
        String organizerName,

        @NotBlank(message = "A razão social é um campo obrigatório.")
        String legalName,

        @NotBlank(message = "O CNPJ do organizador é um campo obrigatório.")
        String CNPJ
    ) {}
