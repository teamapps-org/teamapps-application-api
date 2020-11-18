package org.teamapps.application.api.localization;

import com.ibm.icu.text.Transliterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryBuilder {

	private static Transliterator TRANSLITERATOR = Transliterator.getInstance("Any-Latin; nfd; [:nonspacing mark:] remove; nfc");

	public static void main(String[] args) throws IOException {
		List<String> values = readData().stream()
				.filter(s -> s != null && !s.isBlank())
				.collect(Collectors.toList());;
		List<String> resourceLines = new ArrayList<>();
		List<String> codeLines = new ArrayList<>();
		for (String value : values) {
			if (value.startsWith("::")) {
				codeLines.add("//" + value.substring(2));
				resourceLines.add("# " + value.substring(2));
			} else {
				resourceLines.add(createKey(value) + "=" + value);
				codeLines.add("public static final String " + createConstant(value) + " = \"" + createKey(value) + "\";");
			}
		}

		resourceLines.forEach(System.out::println);
		System.out.println("------");
		codeLines.forEach(System.out::println);
	}

	private static String cleanValue(String value) {
		value = TRANSLITERATOR.transliterate(value);
		value = WordUtils.capitalize(value);
		value = value.replaceAll("\\.\\.\\.", "___");
		value = removeNonAnsi(value);
		value = value.replaceAll(" ", "");
		value = value.substring(0, 1).toLowerCase() + value.substring(1);
		return value;
	}

	private static String createKey(String value) {
		return "org.teamapps.dictionary." + cleanValue(value);
	}

	private static String createConstant(String value) {
		value = cleanValue(value);
		value = value.replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();
		if (value.length() > 36) {
			value = "SENTENCE_" + value.substring(0, 35) + "__";
		}
		return value;
	}

	public static String removeNonAnsi(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int code = c;
			if (c == '_' || c == ' ') {
				sb.append(c);
			} else if (code > 64 && code < 91) {
				sb.append(c);
			} else if (code > 96 && code < 123) {
				sb.append(c);
			} else if (code > 47 && code < 58) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static List<String> readData() throws IOException {
		InputStream resource = DictionaryBuilder.class.getResourceAsStream("dictionary-data.txt");
		return IOUtils.readLines(resource, StandardCharsets.UTF_8);
	}


}



