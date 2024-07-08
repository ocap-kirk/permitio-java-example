package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {

	@GetMapping("/API/1")
	public String index() {
		return "Greetings from Spring Boot 1!";
	}
	@GetMapping("/API/2")
	public String index() {
		return "Greetings from Spring Boot 2!";
	}
	@GetMapping("/API/3")
	public String index() {
		return "Greetings from Spring Boot 3!";
	}
	@GetMapping("/API/4")
	public String index() {
		return "Greetings from Spring Boot 4!";
	}
}