package tech.goticket.backendapi.shared.storage;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {
    @Autowired
    private S3Template template;

    private String bucket = "goticket-dev";

    // Para acessar o bucket via variável de ambiente, será utilizado ao acessar o s3 de prod
    // @Value("${bucket.name}")
    // private String bucket;

    public String upload(FileUpload fileUpload) {

        try (var file = fileUpload.data().getInputStream()) {
            String key = UUID.randomUUID().toString();
            S3Resource uploaded = template.upload(bucket, key, file);
            return key;
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possivel realizar o upload do arquivo.");
        }
    }
}
