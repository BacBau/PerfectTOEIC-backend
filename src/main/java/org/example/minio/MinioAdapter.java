package org.example.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MinioAdapter {

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket.name}")
    String defaultBucketName;

    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    public String uploadFile(MultipartFile file) {
        String filename = generateFileName(file);
        try {
            //minioClient.ignoreCertCheck();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(defaultBucketName).object(filename).stream(
                                    file.getInputStream(), -1, 10485760)
                            .contentType("*")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return filename;
    }

    public String uploadFile(File file, String fileName) {
        try {
            //minioClient.ignoreCertCheck();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(defaultBucketName).object(fileName).stream(
                                    Files.newInputStream(file.toPath()), -1, 10485760)
                            .contentType("*")
                            .build());
            file.delete();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return fileName;
    }

    public String uploadAvatar(MultipartFile file) {
        String filename = generateFileName(file);
        try {
            //minioClient.ignoreCertCheck();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(defaultBucketName).object(filename).stream(
                                    file.getInputStream(), -1, 10485760).contentType("image/jpg").contentType("image/png")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return filename;
    }

    public String getFile(String key) {
        try {
            //minioClient.ignoreCertCheck();
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(defaultBucketName)
                            .object(key)
                            .expiry(24 * 60 * 60)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostConstruct
    public void init() {
    }
}