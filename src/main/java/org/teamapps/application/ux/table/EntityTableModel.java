/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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
package org.teamapps.application.ux.table;

import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.pojo.AbstractUdbQuery;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.pojo.Query;
import org.teamapps.universaldb.record.EntityBuilder;

import java.util.List;
import java.util.function.Supplier;

public class EntityTableModel<ENTITY extends Entity<ENTITY>> extends AbstractRecordTableModel<ENTITY> {

	private final Supplier<Query<ENTITY>> querySupplier;
	private final TableIndex tableIndex;
	private final EntityBuilder<ENTITY> entityBuilder;

	public EntityTableModel(Supplier<Query<ENTITY>> querySupplier) {
		this.querySupplier = querySupplier;
		AbstractUdbQuery<ENTITY> udbQuery = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		tableIndex = udbQuery.getTableIndex();
		entityBuilder = udbQuery.getEntityBuilder();
	}

	@Override
	public List<ENTITY> executeQuery(String fullTextSearchString, Sorting sorting) {
		AbstractUdbQuery<ENTITY> query = (AbstractUdbQuery<ENTITY>) querySupplier.get();
		if (fullTextSearchString != null) {
			query.addFullTextQuery(fullTextSearchString);
		}
		String sortField = sorting != null ? sorting.getFieldName() : null;
		boolean sortAscending = sorting == null || sorting.getSortDirection() == SortDirection.ASC;
		return query.execute(sortField, sortAscending, null);
	}
}
