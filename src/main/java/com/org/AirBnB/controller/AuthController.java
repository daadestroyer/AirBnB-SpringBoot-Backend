package com.org.AirBnB.controller;

import com.org.AirBnB.dto.JwtTokenResponseDto;
import com.org.AirBnB.dto.LoginDto;
import com.org.AirBnB.dto.SignUpDto;
import com.org.AirBnB.dto.UserDTO;
import com.org.AirBnB.exception.APIResponse;
import com.org.AirBnB.exception.customexceptions.BadCredentialsException;
import com.org.AirBnB.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto){
        log.info("Signup Dto details"+signUpDto);
        UserDTO userDto =  authService.signUp(signUpDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            JwtTokenResponseDto jwtTokenResponse = authService.login(loginDto); // may call authenticationManager.authenticate(...)
            Cookie cookie = new Cookie("refreshToken", jwtTokenResponse.getRefreshToken());
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return ResponseEntity.ok(jwtTokenResponse);
        } catch (BadCredentialsException ex) {
            APIResponse api = new APIResponse("Invalid credentials", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
        } catch (RuntimeException ex) {
            // handle wrapper/runtime exceptions too
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof BadCredentialsException) {
                APIResponse api = new APIResponse("Invalid credentials", HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
            }
            throw ex; // rethrow unexpected
        }
    }

}
