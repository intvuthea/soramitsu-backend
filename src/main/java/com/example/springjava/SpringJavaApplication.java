package com.example.springjava;

import com.example.springjava.models.User;
import com.example.springjava.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;

@SpringBootApplication
public class SpringJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJavaApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveUser(new User(null, "Int Vuthea", "john", "123"));
		};
	}
}
