package com.spring.genie.service;

import com.spring.genie.api.dto.GenerateProjectRequest;
import com.spring.genie.generator.DirectoryZip;
import com.spring.genie.generator.SpringBootProjectWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ProjectArchiveService {

	public byte[] buildZip(GenerateProjectRequest req) {
		validateForGeneration(req);
		Path tempParent = null;
		try {
			tempParent = Files.createTempDirectory("spring-genie-");
			Path projectRoot = tempParent.resolve(req.projectName().trim());
			writeProject(req, projectRoot);
			return DirectoryZip.zipProjectFolder(projectRoot);
		}
		catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Failed to generate project: " + e.getMessage(), e);
		}
		finally {
			if (tempParent != null) {
				deleteRecursively(tempParent);
			}
		}
	}

	/**
	 * Same files as the ZIP download: relative path (forward slashes) → UTF-8 contents.
	 */
	public Map<String, String> buildPreviewFiles(GenerateProjectRequest req) {
		validateForGeneration(req);
		Path tempParent = null;
		try {
			tempParent = Files.createTempDirectory("spring-genie-preview-");
			Path projectRoot = tempParent.resolve(req.projectName().trim());
			writeProject(req, projectRoot);
			Map<String, String> out = new LinkedHashMap<>();
			try (var stream = Files.walk(projectRoot)) {
				for (Path path : stream.filter(Files::isRegularFile).sorted().toList()) {
					String rel = projectRoot.relativize(path).toString().replace('\\', '/');
					out.put(rel, Files.readString(path, StandardCharsets.UTF_8));
				}
			}
			return out;
		}
		catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Failed to build preview: " + e.getMessage(), e);
		}
		finally {
			if (tempParent != null) {
				deleteRecursively(tempParent);
			}
		}
	}

	private static void validateForGeneration(GenerateProjectRequest req) {
		String moduleName = req.moduleName().trim();
		if (moduleName.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "moduleName is required");
		}
		String databaseType = req.database().trim().toLowerCase();
		if (!"h2".equals(databaseType)) {
			String dbName = req.databaseName() != null ? req.databaseName().trim() : "";
			if (dbName.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"databaseName is required for MySQL and PostgreSQL");
			}
		}
	}

	private static void writeProject(GenerateProjectRequest req, Path projectRoot) throws IOException {
		String projectName = req.projectName().trim();
		String basePackage = req.basePackage().trim().toLowerCase();
		String buildTool = req.buildTool().trim().toLowerCase();
		String configFileType = req.configType().trim().toLowerCase();
		if ("yaml".equals(configFileType)) {
			configFileType = "yml";
		}
		String databaseType = req.database().trim().toLowerCase();
		String moduleName = req.moduleName().trim();
		boolean createDb = Boolean.TRUE.equals(req.createDatabaseIfNotExists());
		String dbUser = req.dbUsername() != null ? req.dbUsername().trim() : "";
		String dbPass = req.dbPassword() != null ? req.dbPassword() : "";
		String databaseName = req.databaseName() != null ? req.databaseName().trim() : "";
		String dialect = "";

		SpringBootProjectWriter.write(projectRoot, projectName, basePackage, moduleName, buildTool, configFileType,
				databaseType, databaseName, dialect, createDb, dbUser, dbPass);
	}

	private static void deleteRecursively(Path root) {
		try {
			if (Files.isDirectory(root)) {
				try (var stream = Files.walk(root)) {
					stream.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
						try {
							Files.deleteIfExists(p);
						}
						catch (IOException ignored) {
							// best-effort cleanup
						}
					});
				}
			}
		}
		catch (IOException ignored) {
			// best-effort cleanup
		}
	}
}
