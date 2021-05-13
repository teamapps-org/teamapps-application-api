package org.teamapps.application.api.localization;

import java.util.*;

public class LocalizationLanguages {

	private List<Locale> originalLanguages;
	private List<Locale> machineTranslatedLanguages = Collections.emptyList();

	public LocalizationLanguages(Locale... languages) {
		originalLanguages = Arrays.asList(languages);
	}

	public LocalizationLanguages(List<Locale> originalLanguages, List<Locale> machineTranslatedLanguages) {
		this.originalLanguages = originalLanguages;
		this.machineTranslatedLanguages = machineTranslatedLanguages;
	}

	public LocalizationLanguages setMachineTranslatedLanguages(Locale... languages) {
		machineTranslatedLanguages = Arrays.asList(languages);
		return this;
	}

	public List<Locale> getOriginalLanguages() {
		return originalLanguages;
	}

	public List<Locale> getMachineTranslatedLanguages() {
		return machineTranslatedLanguages;
	}

}
