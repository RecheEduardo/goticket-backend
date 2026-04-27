package tech.goticket.backendapi.event.dto;

public record EventCategoryDTO(Long categoryId,
                               String name,
                               String slug) {
}
