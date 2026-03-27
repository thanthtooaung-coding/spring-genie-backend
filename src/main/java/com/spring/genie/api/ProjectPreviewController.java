package com.spring.genie.api;

import com.spring.genie.api.dto.GenerateProjectRequest;
import com.spring.genie.api.dto.PreviewResponse;
import com.spring.genie.service.ProjectArchiveService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = { "*" })
public class ProjectPreviewController {

	private final ProjectArchiveService projectArchiveService;

	public ProjectPreviewController(ProjectArchiveService projectArchiveService) {
		this.projectArchiveService = projectArchiveService;
	}

	@PostMapping(value = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public PreviewResponse preview(@Valid @RequestBody GenerateProjectRequest request) {
		return new PreviewResponse(projectArchiveService.buildPreviewFiles(request));
	}
}
