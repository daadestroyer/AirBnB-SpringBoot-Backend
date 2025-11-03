package com.org.AirBnB.security;
import com.org.AirBnB.dto.JwtTokenResponseDto;
import com.org.AirBnB.dto.LoginDto;
import com.org.AirBnB.dto.SignUpDto;
import com.org.AirBnB.dto.UserDTO;
import com.org.AirBnB.entities.User;
import com.org.AirBnB.entities.enums.Role;
import com.org.AirBnB.repository.UserRepository;
import com.org.AirBnB.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public UserDTO signUp(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + signUpDto.getEmail() + " already exists");
        }
        User user = modelMapper.map(signUpDto, User.class);
        if(signUpDto.getRoles().isEmpty()){
            user.setRoles(Set.of(Role.GUEST));
        }
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        log.info("User details before signup"+user);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public JwtTokenResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return new JwtTokenResponseDto(accessToken,refreshToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
