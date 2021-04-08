package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationBuilder;
import org.teamapps.application.api.localization.ApplicationLocalizationProvider;
import org.teamapps.application.api.localization.LocalizationData;
import org.teamapps.universaldb.index.translation.TranslatableText;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class DevLocalizationProvider implements ApplicationLocalizationProvider {

	private final ApplicationBuilder applicationBuilder;
	private final Map<String, Map<String, String>> localizationMap;
	private final Map<String, Map<String, String>> dictionaryMap;

	public DevLocalizationProvider(ApplicationBuilder applicationBuilder) {
		this.applicationBuilder = applicationBuilder;
		this.localizationMap = applicationBuilder.getLocalizationData() != null ? applicationBuilder.getLocalizationData().createLocalizationMapByLanguage() : new HashMap<>();
		this.dictionaryMap = LocalizationData.createDictionaryData(getClass().getClassLoader()).createLocalizationMapByLanguage();
	}

	@Override
	public String getLocalized(String key, Object... parameters) {
		String localizationValue = getLocalized(key);
		if (parameters != null && parameters.length > 0) {
			try {
				return MessageFormat.format(localizationValue, parameters);
			} catch (Exception e) {
				e.printStackTrace();
				return localizationValue;
			}
		} else {
			return localizationValue;
		}
	}

	private String getLocalized(String key) {
		for (Map<String, String> map : localizationMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		for (Map<String, String> map : dictionaryMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return key;
	}

	@Override
	public String getLocalized(TranslatableText translatableText) {
		return translatableText != null ? translatableText.getText() : null;
	}
}
