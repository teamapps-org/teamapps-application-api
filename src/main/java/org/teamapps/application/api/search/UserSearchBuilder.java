/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.api.search;

import java.util.*;

public class UserSearchBuilder {

	public static final String FIRST_NAME = "first";
	public static final String LAST_NAME = "last";
	public static final String STREET = "street";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String PHONE = "phone";
	public static final String E_MAIL = "email";

	private Map<String, SearchEntry> searchMap = new HashMap<>();

	public void addFirstNameSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(FIRST_NAME, value, searchType, maxScore);
	}

	public void addLastNameSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(LAST_NAME, value, searchType, maxScore);
	}

	public void addStreetSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(STREET, value, searchType, maxScore);
	}

	public void addPostalCodeSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(POSTAL_CODE, value, searchType, maxScore);
	}

	public void addCitySearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(CITY, value, searchType, maxScore);
	}

	public void addCountryCodeSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(COUNTRY_CODE, value, searchType, maxScore);
	}

	public void addPhoneSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(PHONE, value, searchType, maxScore);
	}

	public void addEmailSearch(String value, SearchType searchType, int maxScore) {
		addSearchEntry(E_MAIL, value, searchType, maxScore);
	}

	public void addSearchEntry(String field, String value, SearchType searchType, int maxScore) {
		if (value != null && !value.isBlank() && value.trim().length() >= 2) {
			SearchEntry searchEntry = new SearchEntry(field, value, searchType, maxScore);
			searchMap.put(field, searchEntry);
		}
	}

	public boolean containsAddressFilter() {
		Set<String> fields = new HashSet<>(List.of(STREET, POSTAL_CODE, CITY, COUNTRY_CODE));
		return searchMap.values().stream().anyMatch(f -> fields.contains(f.getField()) && f.getSearchType() != SearchType.OPTIONAL);
	}

	public Map<String, SearchEntry> getSearchMap() {
		return searchMap;
	}

	public int getMaxScore() {
		return searchMap.values().stream().mapToInt(SearchEntry::getMaxScore).sum();
	}

	public int getScore(String field, String value) {
		SearchEntry searchEntry = searchMap.get(field);
		return searchEntry != null ? searchEntry.getScore(value) : 0;
	}

	public boolean containsField(String field) {
		return searchMap.containsKey(field);
	}
}
