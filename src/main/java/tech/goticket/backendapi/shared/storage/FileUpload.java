package tech.goticket.backendapi.shared.storage;

import org.springframework.web.multipart.MultipartFile;

// Abstração de arquivo submetido via request HTTP.

public record FileUpload(MultipartFile data) {
}
