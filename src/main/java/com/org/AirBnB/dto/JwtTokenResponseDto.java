package com.org.AirBnB.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
