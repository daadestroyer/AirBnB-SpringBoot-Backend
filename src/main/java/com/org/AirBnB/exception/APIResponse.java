package com.org.AirBnB.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class APIResponse {
    String message;
    HttpStatus code;


}
