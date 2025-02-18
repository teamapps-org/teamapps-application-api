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

import org.apache.commons.text.similarity.LevenshteinDistance;

public class SearchEntry {

	private final String field;
	private final String value;
	private final SearchType searchType;
	private final int maxScore;

	public SearchEntry(String field, String value, SearchType searchType, int maxScore) {
		this.field = field;
		this.value = value;
		this.searchType = searchType;
		this.maxScore = maxScore;
	}

	public int getScore(String result) {
		if (result == null || result.isEmpty()) {
			return 0;
		}
		int distance = LevenshteinDistance.getDefaultInstance().apply(result.trim().toLowerCase(), value.trim().toLowerCase());
		int size = Math.max(result.length(), value.length());
		if (distance >= size / 2) {
			return 0;
		}
		float scoreValue =  (size - distance) / (float) size;
		return (int) (scoreValue * maxScore);
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public int getMaxScore() {
		return maxScore;
	}

}
