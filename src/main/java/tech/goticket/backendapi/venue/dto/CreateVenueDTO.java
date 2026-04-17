package tech.goticket.backendapi.venue.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;

import java.util.UUID;

public record CreateVenueDTO(
        @NotBlank(message = "O nome do espaço é um campo obrigatório.")
        String name,

        @NotBlank(message = "A razão social do espaço é um campo obrigatório.")
        String legalName,

        @NotBlank(message = "O número de documento do espaço é um campo obrigatório.")
        String CNPJ,

        String description,

        @NotBlank(message = "O logradouro do espaço é um campo obrigatório.")
        String streetAddress,

        @NotBlank(message = "O número do endereço do espaço é um campo obrigatório.")
        String streetAddressNumber,

        @NotBlank(message = "O bairro do espaço é um campo obrigatório.")
        String neighborhood,

        @NotBlank(message = "A cidade do espaço é um campo obrigatório.")
        String city,

        @NotBlank(message = "O estado do espaço é um campo obrigatório.")
        String state,

        @NotBlank(message = "O país do espaço é um campo obrigatório.")
        String country,

        @NotBlank(message = "O cep do espaço é um campo obrigatório.")
        String zipCode,

        UUID organizerID
) {
    public CreateVenueDTO {
        description = StringUtils.hasText(description)
                ? description.trim()
                : "";
    }
}
