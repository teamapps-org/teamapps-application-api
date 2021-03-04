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
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityListModelBuilder<ENTITY> extends RecordModelBuilder<ENTITY> {

	private List<ENTITY> entities = Collections.emptyList();
	private Function<ENTITY, String> entityStringFunction;
	private Function<ENTITY, Long> entityDateInMillisFunction;

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<ENTITY, String> entityStringFunction) {
		super(applicationInstanceData);
		this.entityStringFunction = entityStringFunction;
	}

	public EntityListModelBuilder(ApplicationInstanceData applicationInstanceData, Function<ENTITY, String> entityStringFunction, Function<ENTITY, Long> entityDateInMillisFunction) {
		super(applicationInstanceData);
		this.entityStringFunction = entityStringFunction;
		this.entityDateInMillisFunction = entityDateInMillisFunction;
	}

	public void setEntityStringFunction(Function<ENTITY, String> entityStringFunction) {
		this.entityStringFunction = entityStringFunction;
	}

	public void setEntityDateInMillisFunction(Function<ENTITY, Long> entityDateInMillisFunction) {
		this.entityDateInMillisFunction = entityDateInMillisFunction;
	}

	public List<ENTITY> getEntities() {
		return entities;
	}

	public void setEntities(List<ENTITY> entities) {
		this.entities = entities;
		onDataChanged.fire();
	}

	@Override
	public List<ENTITY> queryRecords(String fullTextQuery, TimeIntervalFilter timeIntervalFilter, String sortField, boolean sortAscending) {
		List<ENTITY> filteredEntities = null;
		if (entityStringFunction != null && fullTextQuery != null && !fullTextQuery.isBlank()) {
			String query = fullTextQuery.toLowerCase();
			filteredEntities = entities.stream().filter(entity -> match(entityStringFunction.apply(entity), query)).collect(Collectors.toList());
		}
		if (entityDateInMillisFunction != null && timeIntervalFilter != null) {
			if (filteredEntities == null) {
				filteredEntities = entities;
			}
			filteredEntities = filteredEntities.stream().filter(entity -> match(timeIntervalFilter, entityDateInMillisFunction.apply(entity))).collect(Collectors.toList());
		}

		if (filteredEntities == null) {
			filteredEntities = entities;
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