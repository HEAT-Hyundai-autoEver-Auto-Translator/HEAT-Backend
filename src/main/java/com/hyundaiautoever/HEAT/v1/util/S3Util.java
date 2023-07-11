package com.hyundaiautoever.HEAT.v1.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
@Generated
public class S3Util {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    public String uploadUserProfileImage(Optional<MultipartFile> multipartFile) throws IOException {
        if(!multipartFile.isPresent() || multipartFile.get().isEmpty()) {
            return null;
        }
        File uploadFile = convertFile(multipartFile.get())
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile);
    }


    private String upload(File uploadFile) {
        String fileName = "profile-image/" + UUID.randomUUID().toString() + "/" + uploadFile.getName();
        String uploadImageUrl = putImageOnS3(uploadFile, fileName);
        // 로컬 임시 저장 File 삭제
        uploadFile.delete();

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }


    private String putImageOnS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(BUCKET_NAME, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(BUCKET_NAME, fileName).toString();
    }


    private Optional<File> convertFile(MultipartFile file) throws  IOException {
        log.info(file.getOriginalFilename());
        File convertFileFile = new File(file.getOriginalFilename());
        if(convertFileFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFileFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFileFile);
        }
        return Optional.empty();
    }


    public void removeS3File(String url) {
        if (url == null) {return;}
        String prefix = "https://heat-back-s3.s3.ap-northeast-2.amazonaws.com/";
        String key = url.substring(prefix.length());
        amazonS3.deleteObject(BUCKET_NAME, key);
    }
}