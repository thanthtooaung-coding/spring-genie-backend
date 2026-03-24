package com.spring.genie.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the same tree as {@code CliGenerator} from
 * <a href="https://github.com/thanthtooaung-coding/spring-genie">spring-genie</a> into {@code projectRoot}.
 */
public final class SpringBootProjectWriter {

	private static final String SRC_MAIN_JAVA = "src/main/java";
	private static final String SRC_MAIN_RESOURCES = "src/main/resources";

	private SpringBootProjectWriter() {
	}

	public static void write(
			Path projectRoot,
			String projectName,
			String basePackageLower,
			String moduleNameRaw,
			String buildTool,
			String configFileType,
			String databaseType,
			String databaseName,
			String databaseDialect,
			boolean createDatabaseIfNotExist,
			String dbUsername,
			String dbPassword) throws IOException {

		String pascalCaseModuleName = ProjectNameFormatting.toPascalCase(moduleNameRaw);
		String camelCaseModuleName = ProjectNameFormatting.toCamelCase(moduleNameRaw);

		Files.createDirectories(projectRoot);

		if ("gradle".equalsIgnoreCase(buildTool)) {
			writeFile(projectRoot.resolve("build.gradle"),
					GradleBuildFileGenerator.generate(projectName, basePackageLower, databaseType, camelCaseModuleName));
		}
		else {
			writeFile(projectRoot.resolve("pom.xml"),
					PomXmlGenerator.generate(projectName, basePackageLower, databaseType));
		}

		Path javaBasePath = projectRoot.resolve(SRC_MAIN_JAVA).resolve(basePackageLower.replace(".", File.separator));
		Path moduleBasePath = javaBasePath.resolve(camelCaseModuleName);

		Files.createDirectories(moduleBasePath.resolve("config"));
		Files.createDirectories(moduleBasePath.resolve("controller"));
		Files.createDirectories(moduleBasePath.resolve("service"));
		Files.createDirectories(moduleBasePath.resolve("repository"));
		Files.createDirectories(moduleBasePath.resolve("entity"));

		writeFile(moduleBasePath.resolve("Application.java"),
				ApplicationClassGenerator.generate(basePackageLower, pascalCaseModuleName));
		writeFile(moduleBasePath.resolve("config").resolve("OpenApiConfig.java"),
				OpenApiConfigGenerator.generate(basePackageLower, pascalCaseModuleName));
		writeFile(moduleBasePath.resolve("entity").resolve(pascalCaseModuleName + ".java"),
				EntityClassGenerator.generate(basePackageLower, pascalCaseModuleName));
		writeFile(moduleBasePath.resolve("repository").resolve(pascalCaseModuleName + "Repository.java"),
				RepositoryClassGenerator.generate(basePackageLower, pascalCaseModuleName));
		writeFile(moduleBasePath.resolve("service").resolve(pascalCaseModuleName + "Service.java"),
				ServiceClassGenerator.generate(basePackageLower, pascalCaseModuleName));
		writeFile(moduleBasePath.resolve("controller").resolve(pascalCaseModuleName + "Controller.java"),
				ControllerClassGenerator.generate(basePackageLower, pascalCaseModuleName));

		Path resourcesPath = projectRoot.resolve(SRC_MAIN_RESOURCES);
		Files.createDirectories(resourcesPath);
		String fileName = "application." + configFileType;
		writeFile(resourcesPath.resolve(fileName),
				ApplicationConfigGenerator.generate(configFileType, databaseType, databaseName, databaseDialect,
						createDatabaseIfNotExist, dbUsername != null ? dbUsername : "",
						dbPassword != null ? dbPassword : ""));
	}

	private static void writeFile(Path path, String content) throws IOException {
		Files.createDirectories(path.getParent());
		Files.writeString(path, content, StandardCharsets.UTF_8);
	}
}
