package com.hyundaiautoever.HEAT.v1.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
@Generated
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    public String uploadUserProfileImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null) {
            log.info("file is null");
        }
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile);
    }


    private String upload(File uploadFile) {
        String fileName = "profile-image/" + UUID.randomUUID().toString() + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        // 로컬 임시 저장 File 삭제
        uploadFile.delete();

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }


    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(BUCKET_NAME, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(BUCKET_NAME, fileName).toString();
    }


    private Optional<File> convert(MultipartFile file) throws  IOException {
        log.info(file.getOriginalFilename());
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }


    public void removeS3File(String url) {
        String prefix = "https://heat-back-s3.s3.ap-northeast-2.amazonaws.com/";
        String key = url.substring(prefix.length());
        amazonS3.deleteObject(BUCKET_NAME, key);
    }
}