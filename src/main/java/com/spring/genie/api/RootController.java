package com.spring.genie.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> root() {
		return Map.of(
				"status", "ok",
				"service", "spring-genie",
				"message", "Spring Genie backend is running"
		);
	}
}

