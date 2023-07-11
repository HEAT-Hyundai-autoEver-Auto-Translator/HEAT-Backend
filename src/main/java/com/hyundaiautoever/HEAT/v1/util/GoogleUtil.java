package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.dto.user.GoogleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleUtil {

    private static final String GOOGLE_API = "https://www.googleapis.com/oauth2/v1/userinfo";

    public GoogleResponseDto getUserInfo(String googleAccessToken) throws IOException {

        WebClient client = WebClient.builder().baseUrl(GOOGLE_API).build();

        GoogleResponseDto googleResponseDto = client.get()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + googleAccessToken)
                .retrieve()
                .bodyToMono(GoogleResponseDto.class)
                .block();

        if (googleResponseDto == null || googleResponseDto.getEmail() == null) {
            throw new IOException("Google API 호출 실패");
        }

        log.info(googleResponseDto.toString());

        return googleResponseDto;
    }


}
