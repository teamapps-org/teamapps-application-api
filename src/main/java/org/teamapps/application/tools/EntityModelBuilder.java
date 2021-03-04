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
import org.teamapps.universaldb.index.IndexType;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.numeric.NumericFilter;
import org.teamapps.universaldb.pojo.AbstractUdbQuery;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.List;
import java.util.function.Supplier;

public class EntityModelBuilder<ENTITY extends Entity<ENTITY>> extends RecordModelBuilder<ENTITY> {

	private final Supplier<Query<ENTITY>> querySupplier;
	private final TableIndex tableIndex;
	private final EntityBuilder<ENTITY> entityBuilder;

	public EntityModelBuilder(Supplier<Query<ENTITY>> querySupplier, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.querySupplier = querySupplier;
		AbstractUdbQuery<ENTITY> udbQuery = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		tableIndex = udbQuery.getTableIndex();
		entityBuilder = udbQuery.getEntityBuilder();
	}

	@Override
	public List<ENTITY> queryRecords(String fullTextQuery, TimeIntervalFilter timeIntervalFilter, String sortField, boolean sortAscending) {
		AbstractUdbQuery<ENTITY> query = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		if (timeIntervalFilter != null) {
			NumericFilter numericFilter = tableIndex.getColumnIndex(timeIntervalFilter.getFieldName()).getType() == IndexType.INT ? timeIntervalFilter.getIntFilter() : timeIntervalFilter.getFilter();
			query.addNumericFilter(timeIntervalFilter.getFieldName(), numericFilter);
		}
		if (fullTextQuery != null && !fullTextQuery.isBlank()) {
			query.addFullTextQuery(fullTextQuery);
		}
		return sortField != null ? query.execute(sortField, sortAscending) : query.execute();
	}
}
