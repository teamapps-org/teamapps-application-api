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
