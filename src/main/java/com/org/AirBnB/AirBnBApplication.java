package com.org.AirBnB;

import com.org.AirBnB.entities.User;
import com.org.AirBnB.entities.enums.Role;
import com.org.AirBnB.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class AirBnBApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(AirBnBApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final String adminEmail = "nigamshubham@gmail.com";

		boolean exists = userRepository.findByEmail(adminEmail).isPresent();
		if (exists) {
			// already present, nothing to do
			return;
		}
		User admin = User.builder()
				.email(adminEmail)
				.name("Shubham Nigam")
				.password(passwordEncoder.encode("1234")) // encode password!
				.roles(Set.of(Role.ADMIN))
				.build();

		userRepository.save(admin);
		System.out.println("Created default admin user: " + adminEmail);
	}
}
