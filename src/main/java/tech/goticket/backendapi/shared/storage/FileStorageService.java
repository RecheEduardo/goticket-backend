package tech.goticket.backendapi.shared.storage;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FileStorageService {
    @Autowired
    private S3Template template;

    @Value("${bucket.name}")
    private String bucket;

    @Value("${spring.cloud.aws.endpoint}")
    private String endpoint;

    public String upload(FileUpload fileUpload) {
        String key = UUID.randomUUID().toString();
        return uploadWithKey(fileUpload, key);
    }

    public String uploadWithKey(FileUpload fileUpload, String key) {
        try (var file = fileUpload.data().getInputStream()) {
            S3Resource uploaded = template.upload(bucket, key, file);
            return uploaded.getFilename();
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possivel realizar o upload do arquivo.");
        }
    }

    public void delete(String s3Key) {
        template.deleteObject(bucket, s3Key);
    }

    public String resolvePublicUrl(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            return null;
        }
        return endpoint.replaceAll("/$", "") + "/" + bucket + "/" + s3Key;
    }

    public String getObjectAsText(String s3Key) {
        try (var input = template.download(bucket, s3Key).getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível ler o arquivo do storage.");
        }
    }
}
