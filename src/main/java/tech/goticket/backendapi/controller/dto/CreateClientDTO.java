package tech.goticket.backendapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateClientDTO(
        @NotBlank(message = "O e-mail é um campo obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "A senha é um campo obrigatório.")
        String password,

        @NotBlank(message = "O nome completo é um campo obrigatório.")
        String fullName,

        @NotNull(message = "O sexo do cliente é um campo obrigatório.")
        Integer sex,

        @NotBlank(message = "O documento do cliente é um campo obrigatório.")
        String identityDocument,

        @NotBlank(message = "A data de nascimento do cliente é um campo obrigatório.")
        String birthDate
    ) {}
