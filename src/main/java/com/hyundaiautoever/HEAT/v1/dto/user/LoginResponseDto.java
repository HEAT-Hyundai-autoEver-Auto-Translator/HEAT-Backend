package com.hyundaiautoever.HEAT.v1.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@EqualsAndHashCode
@Schema
public class LoginResponseDto {

    @Schema(description = "유저 인덱스", example = "1")
    private Long userAccountNo;
    @Schema(description = "유저 액세스 토큰", example = "eyJ0eXAiOiJKV1Q ... uyBAQxfD1-fGOkdhl2R5IuNSk")
    private String accessToken;
    @Schema(description = "유저 리프레시 토큰", example = "eyJ0eXAiOiJKV1Q ... uyBAQxfD1-fGOkdhl2R5IuNSk")
    private String refreshToken;
}
