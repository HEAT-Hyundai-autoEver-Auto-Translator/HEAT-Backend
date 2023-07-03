package com.hyundaiautoever.HEAT.v1.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.querydsl.core.annotations.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String AWS_ACCESS_KEY_ID;
    @Value("${cloud.aws.credentials.secret-key}")
    private String AWS_SECRET_ACCESS_KEY;
    private final String AWS_REGION = "ap-northeast-2";

    @Bean
    public AmazonS3 amazonS3Client() {

        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(AWS_REGION)
                .build();
    }
}
