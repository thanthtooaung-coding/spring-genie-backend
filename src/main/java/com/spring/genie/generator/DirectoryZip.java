package com.spring.genie.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class DirectoryZip {

	private DirectoryZip() {
	}

	/**
	 * Zips {@code projectRoot} so that entries are {@code projectRoot.getFileName()/relativePath} (forward slashes).
	 */
	public static byte[] zipProjectFolder(Path projectRoot) throws IOException {
		String top = projectRoot.getFileName().toString();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos);
				var paths = Files.walk(projectRoot)) {
			for (Path path : paths.filter(Files::isRegularFile).toList()) {
				String relative = projectRoot.relativize(path).toString().replace('\\', '/');
				String entryName = top + "/" + relative;
				zos.putNextEntry(new ZipEntry(entryName));
				zos.write(Files.readAllBytes(path));
				zos.closeEntry();
			}
		}
		return baos.toByteArray();
	}
}
