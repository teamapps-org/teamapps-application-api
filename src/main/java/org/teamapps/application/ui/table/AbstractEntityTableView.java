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
package org.teamapps.application.ui.table;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.AbstractPerspectiveModel;
import org.teamapps.data.value.SortDirection;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.table.ListTable;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractEntityTableView<ENTITY extends Entity<ENTITY>> extends AbstractLazyRenderingApplicationView  {


	private final AbstractPerspectiveModel<ENTITY> perspectiveModel;
	private boolean initialised;
	private ListTable<ENTITY> table;

	public AbstractEntityTableView(AbstractPerspectiveModel<ENTITY> perspectiveModel, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.perspectiveModel = perspectiveModel;

		onViewRedrawRequired.addListener(() -> {
			if (!initialised) {
				initialised = true;

			}
		});
	}

	@Override
	public void createUi() {
		table = new  ListTable<>();
		table.setDisplayAsList(true);
		table.setRowHeight(28);

		String baseTitle = getParentPanel().getTitle();
		getParentPanel().setTitle((perspectiveModel.getShowDeletedEntities().get() ? "🗑" + getLocalized("apps.deleted") + ": " : "") + baseTitle + " (" + getLocalizedFormatter().formatDecimalNumber(Optional.ofNullable(perspectiveModel.getEntityCount()).map(ObservableValue::get).orElse(0))+")");
		perspectiveModel.getEntityCount().onChanged().addListener(entityCount -> getParentPanel().setTitle((perspectiveModel.getShowDeletedEntities().get() ? "🗑" + getLocalized("apps.deleted") + ": " : "") + baseTitle + " (" + getLocalizedFormatter().formatDecimalNumber(entityCount)+")"));
		perspectiveModel.setFullTextFilterField(getParentPanel());
		onViewRedrawRequired.addListener(() -> table.setRecords(perspectiveModel.getEntities().get()));
		perspectiveModel.getEntities().onChanged().addListener(entities -> {
			if (isVisible()) {
				table.setRecords(entities);
			}
		});
		perspectiveModel.getSelectedEntity().onChanged().addListener(entity -> {
			if (!Objects.equals(entity, table.getSelectedRecord())) {
				table.setSelectedRecord(entity);
			}
		});
		table.onSingleRowSelected.addListener(record -> {
			perspectiveModel.handleEntitySelection(record);
			focusTargetView();
		});
		table.onSortingChanged.addListener(event -> {
			perspectiveModel.handleSortRequest(event.getSortField(), event.getSortDirection() == SortDirection.ASC);
		});
		createTable(table);
	}

	public abstract void createTable(ListTable<ENTITY> table);

	public void addSorter(String fieldName, Comparator<ENTITY> comparator) {
		perspectiveModel.addSorter(fieldName, comparator);
	}

	public void addStringSorter(String fieldName, Function<ENTITY, String> entityStringFunction) {
		perspectiveModel.addStringSorter(fieldName, entityStringFunction);
	}

	public void addIntegerSorter(String fieldName, Function<ENTITY, Integer> entityIntegerFunction) {
		perspectiveModel.addIntegerSorter(fieldName, entityIntegerFunction);
	}

	public void addLongSorter(String fieldName, Function<ENTITY, Long> entityLongFunction) {
		perspectiveModel.addLongSorter(fieldName, entityLongFunction);
	}

	public void addBooleanSorter(String fieldName, Function<ENTITY, Boolean> entityBooleanFunction) {
		perspectiveModel.addBooleanSorter(fieldName, entityBooleanFunction);
	}
	
	public ListTable<ENTITY> getTable() {
		return table;
	}

	@Override
	public Component getViewComponent() {
		return table;
	}
}
