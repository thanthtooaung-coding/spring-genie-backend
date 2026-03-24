package com.spring.genie.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateProjectRequest(
		@NotBlank @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$", message = "projectName must be alphanumeric with optional hyphens") String projectName,
		@NotBlank @Pattern(regexp = "^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$", message = "basePackage must be a valid Java package") String basePackage,
		@NotBlank String moduleName,
		@Pattern(regexp = "(?i)maven|gradle", message = "buildTool must be maven or gradle") String buildTool,
		@Pattern(regexp = "(?i)properties|yml", message = "configType must be properties or yml") String configType,
		@Pattern(regexp = "(?i)h2|mysql|postgresql", message = "database must be h2, mysql, or postgresql") String database,
		String databaseName,
		Boolean createDatabaseIfNotExists,
		String dbUsername,
		String dbPassword) {

	public GenerateProjectRequest {
		projectName = projectName == null ? "" : projectName.trim();
		basePackage = basePackage == null ? "" : basePackage.trim().toLowerCase();
		moduleName = moduleName == null ? "" : moduleName.trim();
		if (buildTool == null || buildTool.isBlank()) {
			buildTool = "maven";
		}
		else {
			buildTool = buildTool.trim().toLowerCase();
		}
		if (configType == null || configType.isBlank()) {
			configType = "properties";
		}
		else {
			configType = configType.trim().toLowerCase();
		}
		if (database == null || database.isBlank()) {
			database = "h2";
		}
		else {
			database = database.trim().toLowerCase();
		}
	}
}
