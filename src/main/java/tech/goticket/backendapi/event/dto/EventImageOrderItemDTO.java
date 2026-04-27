package tech.goticket.backendapi.event.dto;

public record EventImageOrderItemDTO(
        String type,
        String s3Key,
        Integer fileIndex
) {
}
