package com.spring.genie.generator;

final class ProjectNameFormatting {

	private ProjectNameFormatting() {
	}

	static String toPascalCase(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = true;
		for (char c : input.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				}
				else {
					result.append(Character.toLowerCase(c));
				}
			}
			else {
				capitalizeNext = true;
			}
		}
		return result.toString();
	}

	static String toCamelCase(String input) {
		String pascalCase = toPascalCase(input);
		if (pascalCase.isEmpty()) {
			return "";
		}
		return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
	}
}
