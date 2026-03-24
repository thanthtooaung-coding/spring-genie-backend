package com.spring.genie.api;

import com.spring.genie.api.dto.GenerateProjectRequest;
import com.spring.genie.service.ProjectArchiveService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" })
public class ProjectGeneratorController {

	private final ProjectArchiveService projectArchiveService;

	public ProjectGeneratorController(ProjectArchiveService projectArchiveService) {
		this.projectArchiveService = projectArchiveService;
	}

	@PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/zip")
	public ResponseEntity<byte[]> generate(@Valid @RequestBody GenerateProjectRequest request) {
		byte[] zip = projectArchiveService.buildZip(request);
		String filename = request.projectName() + ".zip";
		ContentDisposition disposition = ContentDisposition.attachment()
				.filename(filename, StandardCharsets.UTF_8)
				.build();
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
				.contentType(MediaType.parseMediaType("application/zip"))
				.contentLength(zip.length)
				.body(zip);
	}
}
