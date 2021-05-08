/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.tools;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.udb.filter.TimeIntervalFilter;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityListModelBuilder<RECORD> extends RecordModelBuilder<RECORD> {

	private List<RECORD> records = Collections.emptyList();
	private Function<RECORD, String> entityStringFunction;
	private Function<RECORD, Long> entityDateInMillisFunction;

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<RECORD, String> entityStringFunction) {
		super(applicationInstanceData);
		this.entityStringFunction = entityStringFunction;
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<RECORD, String> entityStringFunction, Function<RECORD, Long> entityDateInMillisFunction) {
		super(applicationInstanceData);
		this.entityStringFunction = entityStringFunction;
		this.entityDateInMillisFunction = entityDateInMillisFunction;
	}

	public void setEntityStringFunction(Function<RECORD, String> entityStringFunction) {
		this.entityStringFunction = entityStringFunction;
	}

	public void setEntityDateInMillisFunction(Function<RECORD, Long> entityDateInMillisFunction) {
		this.entityDateInMillisFunction = entityDateInMillisFunction;
	}

	public List<RECORD> getRecords() {
		return records;
	}

	public void setRecords(List<RECORD> records) {
		this.records = records;
		onDataChanged.fire();
	}

	public void addRecord(RECORD record) {
		records.add(record);
		onDataChanged.fire();
	}

	public void addRecords(List<RECORD> records) {
		records.addAll(records);
		onDataChanged.fire();
	}

	public void removeRecord(RECORD record) {
		records.remove(record);
		onDataChanged.fire();
	}

	public void removeRecords(List<RECORD> records) {
		records.removeAll(records);
		onDataChanged.fire();
	}


	@Override
	public List<RECORD> queryRecords(String fullTextQuery, TimeIntervalFilter timeIntervalFilter) {
		List<RECORD> filteredEntities = null;
		if (entityStringFunction != null && getCustomFullTextFilter() == null && fullTextQuery != null && !fullTextQuery.isBlank()) {
			String query = fullTextQuery.toLowerCase();
			filteredEntities = records.stream().filter(RECORD -> match(entityStringFunction.apply(RECORD), query)).collect(Collectors.toList());
		}
		if (entityDateInMillisFunction != null && timeIntervalFilter != null) {
			if (filteredEntities == null) {
				filteredEntities = records;
			}
			filteredEntities = filteredEntities.stream().filter(RECORD -> match(timeIntervalFilter, entityDateInMillisFunction.apply(RECORD))).collect(Collectors.toList());
		}

		if (filteredEntities == null) {
			filteredEntities = records;
		}

		if (getCustomFullTextFilter() != null && fullTextQuery != null && !fullTextQuery.isBlank()) {
			String query = fullTextQuery.toLowerCase();
			BiFunction<RECORD, String, Boolean> customFullTextFilter = getCustomFullTextFilter();
			filteredEntities = records.stream().filter(RECORD -> customFullTextFilter.apply(RECORD, query)).collect(Collectors.toList());
		}
		return filteredEntities;
	}

	private boolean match(String text, String query) {
		if (text == null) {
			return false;
		}
		return text.toLowerCase().contains(query);
	}

	private boolean match(TimeIntervalFilter intervalFilter, Long date) {
		if (date == null) {
			return false;
		}
		return date >= intervalFilter.getStart() && date <= intervalFilter.getEnd();
	}
}
