package org.teamapps.application.api.localization;

import org.teamapps.ux.i18n.TeamAppsResourceBundleControl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface LocalizationData {

	String DICTIONARY_PREFIX = "org.teamapps.dictionary.";


	List<LocalizationEntrySet> getLocalizationEntrySets();

	default Set<String> getAllKeys() {
		return getLocalizationEntrySets().stream()
				.flatMap(entrySet -> entrySet.getEntries().stream())
				.map(LocalizationEntry::getKey)
				.collect(Collectors.toSet());
	}

	default Set<String> getLanguageSet() {
		return getLocalizationEntrySets().stream()
				.map(LocalizationEntrySet::getLanguage)
				.collect(Collectors.toSet());
	}

	default boolean containsAnyLanguage(List<String> languages) {
		Set<String> languageSet = getLanguageSet();
		for (String language : languages) {
			if (languageSet.contains(language)) {
				return true;
			}
		}
		return false;
	}

	default Map<String, Map<String, String>> createLocalizationMap() {
		Map<String, Map<String, String>> localizationMap = new HashMap<>();
		for (LocalizationEntrySet entrySet : getLocalizationEntrySets()) {
			Map<String, String> map = localizationMap.computeIfAbsent(entrySet.getLanguage(), s -> new HashMap<>());
			entrySet.getEntries().forEach(entry -> map.put(entry.getKey(), entry.getValue()));
		}
		return localizationMap;
	}

	static LocalizationData createFromResourceBundle(ResourceBundle resourceBundle, Locale language) {
		return create(locale -> resourceBundle, language);
	}

	static LocalizationData createFromPropertyFiles(String baseName, Locale... translations) {
		return createFromPropertyFiles(baseName, "properties", translations);
	}

	static LocalizationData createFromPropertyFiles(String baseName, String resourceFileSuffix, Locale... translations) {
		Function<Locale, ResourceBundle> resourceBundleByLocaleFunction = locale -> ResourceBundle.getBundle(baseName, locale, new TeamAppsResourceBundleControl(resourceFileSuffix, Locale.ENGLISH));
		return create(resourceBundleByLocaleFunction, translations);
	}

	static LocalizationData create(Function<Locale, ResourceBundle> resourceBundleByLocaleFunction, Locale... translations) {
		List<LocalizationEntrySet> entrySets = new ArrayList<>();
		for (Locale translation : translations) {
			ResourceBundle resourceBundle = resourceBundleByLocaleFunction.apply(translation);
			entrySets.add(createEntrySet(resourceBundle, translation));
		}
		return () -> entrySets;
	}

	static LocalizationData createDictionaryData() {
		return createFromPropertyFiles("org.teamapps.application.api.localization.dictionary", Locale.ENGLISH);
	}

	static LocalizationEntrySet createEntrySet(ResourceBundle bundle, Locale translation) {
		LocationEntrySetImpl entrySet = new LocationEntrySetImpl(translation.getLanguage());
		if (bundle instanceof PropertyResourceBundle) {
			PropertyResourceBundle propertyResourceBundle = (PropertyResourceBundle) bundle;
			for (String key : bundle.keySet()) {
				Object result = propertyResourceBundle.handleGetObject(key);
				if (result != null) {
					String value = bundle.getString(key);
					if (checkNotEmpty(translation.getLanguage(), key, value)) {
						entrySet.addEntry(key, value);
					}
				}
			}
		} else {
			for (String key : bundle.keySet()) {
				String value = bundle.getString(key);
				if (checkNotEmpty(translation.getLanguage(), key, value)) {
					entrySet.addEntry(key, value);
				}
			}
		}
		return entrySet;
	}

	static boolean checkNotEmpty(String... values) {
		for (String value : values) {
			boolean result = checkNotEmpty(value);
			if (!result) {
				return false;
			}
		}
		return true;
	}

	static boolean checkNotEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}
