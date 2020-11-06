package org.teamapps.application.api.localization;

import java.util.ArrayList;
import java.util.List;

public class LocationEntrySetImpl implements LocalizationEntrySet {

	private final String language;
	private List<LocalizationEntry> entries = new ArrayList();

	public LocationEntrySetImpl(String language) {
		this.language = language;
	}

	public void addEntry(String key, String value) {
		entries.add(new LocalizationEntryImpl(key, value));
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public List<LocalizationEntry> getEntries() {
		return entries;
	}
}
