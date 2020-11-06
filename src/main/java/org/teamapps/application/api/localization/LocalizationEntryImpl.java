package org.teamapps.application.api.localization;

public class LocalizationEntryImpl implements LocalizationEntry {

	private final String key;
	private final String value;

	public LocalizationEntryImpl(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}
}
