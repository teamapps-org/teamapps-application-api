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
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.dto.UiTimeGraph;
import org.teamapps.event.Event;
import org.teamapps.udb.filter.TimeIntervalFilter;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.infiniteitemview.AbstractInfiniteItemViewModel;
import org.teamapps.ux.component.infiniteitemview.InfiniteItemView2;
import org.teamapps.ux.component.infiniteitemview.InfiniteItemViewModel;
import org.teamapps.ux.component.table.AbstractTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.table.TableColumn;
import org.teamapps.ux.component.table.TableModel;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.timegraph.*;
import org.teamapps.ux.component.timegraph.partitioning.PartitioningTimeGraphModel;
import org.teamapps.ux.component.timegraph.partitioning.StaticRawTimedDataModel;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.component.tree.TreeNodeInfoImpl;
import org.teamapps.ux.model.AbstractTreeModel;
import org.teamapps.ux.model.TreeModel;
import org.teamapps.ux.session.SessionContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class RecordModelBuilder<RECORD> {

	public Event<Void> onDataChanged = new Event<>();
	public Event<RECORD> onSelectedRecordChanged = new Event<>();

	private final ApplicationInstanceData applicationInstanceData;
	private TimeIntervalFilter timeIntervalFilter;
	private String fullTextQuery;
	private String sortField;
	private boolean sortAscending;
	private Predicate<RECORD> customFilter;
	private Function<String, Comparator<RECORD>> customFieldSorter;
	private BiFunction<RECORD, String, Boolean> customFullTextFilter;

	private int countRecords;
	private List<RECORD> records;
	private List<RECORD> timeGraphRecords;
	private TwoWayBindableValue<RECORD> selectedRecord = TwoWayBindableValue.create();
	private TwoWayBindableValue<Integer> selectedRecordPosition = TwoWayBindableValue.create();


	public RecordModelBuilder(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;
		onDataChanged.addListener(this::queryRecords);
		onSelectedRecordChanged.addListener(record -> selectedRecord.set(record));
	}

	public void setSelectedRecord(RECORD record) {
		onSelectedRecordChanged.fire(record);
	}

	public RECORD getSelectedRecord() {
		return selectedRecord.get();
	}

	public boolean selectPreviousRecord() {
		if (countRecords == 0) {
			return false;
		}
		RECORD selectedRecord = this.selectedRecord.get();
		if (selectedRecord == null) {
			selectedRecord = records.get(0);
			selectedRecordPosition.set(0);
			onSelectedRecordChanged.fire(selectedRecord);
		}
		int recordPosition = findRecordPosition(selectedRecord);
		if (recordPosition <= 0) {
			return false;
		} else {
			recordPosition--;
			RECORD record = records.get(recordPosition);
			selectedRecordPosition.set(recordPosition);
			onSelectedRecordChanged.fire(record);
			return true;
		}
	}

	public boolean selectNextRecord() {
		if (countRecords == 0) {
			return false;
		}
		RECORD selectedRecord = this.selectedRecord.get();
		if (selectedRecord == null) {
			selectedRecord = records.get(0);
			selectedRecordPosition.set(0);
			onSelectedRecordChanged.fire(selectedRecord);
		}
		int recordPosition = findRecordPosition(selectedRecord);
		if (recordPosition < 0) {
			return false;
		} else {
			recordPosition++;
			if (recordPosition >= records.size()) {
				return false;
			}
			RECORD record = records.get(recordPosition);
			selectedRecordPosition.set(recordPosition);
			onSelectedRecordChanged.fire(record);
			return true;
		}
	}

	private int findRecordPosition(RECORD record) {
		if (record == null) {
			return -1;
		}
		Integer position = selectedRecordPosition.get();
		if (position != null && position < countRecords) {
			if (records.get(position).equals(record)) {
				return position;
			}
		}
		for (int i = 0; i < records.size(); i++) {
			if (record.equals(records.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public void updateModels() {
		onDataChanged.fire();
	}

	public void attachViewCountHandler(View view, Supplier<String> titleSupplier) {
		onDataChanged.addListener(() -> {
			view.getPanel().setTitle(titleSupplier.get() + " (" + countRecords + ")");
		});
	}

	public void attachSearchField(View view) {
		TextField searchField = createSearchField();
		view.getPanel().setRightHeaderField(searchField);
		view.getPanel().setRightHeaderFieldIcon(ApplicationIcons.FUNNEL);
	}

	public TextField createSearchField() {
		TextField searchField = new TextField();
		searchField.setEmptyText(applicationInstanceData.getLocalized(Dictionary.SEARCH___));
		searchField.setShowClearButton(true);
		searchField.onTextInput.addListener(this::setFullTextQuery);
		return searchField;
	}

	public TableModel<RECORD> createTableModel() {
		AbstractTableModel<RECORD> model = new AbstractTableModel<>() {
			@Override
			public int getCount() {
				return countRecords;
			}

			@Override
			public List<RECORD> getRecords(int startIndex, int length, Sorting sorting) {
				return records == null ? Collections.emptyList() : records.stream().skip(startIndex).limit(length).collect(Collectors.toList());
			}
		};
		onDataChanged.addListener((Runnable) model.onAllDataChanged::fire);
		return model;
	}

	public Table<RECORD> createTable() {
		Table<RECORD> table = new Table<>();
		table.setModel(createTableModel());
		table.onSortingChanged.addListener(event -> setSorting(event.getSortField(), event.getSortDirection() == SortDirection.ASC));
		table.onRowSelected.addListener(record -> onSelectedRecordChanged.fire(record));
		return table;
	}

	public Table<RECORD> createTemplateFieldTableList(Template template, PropertyProvider<RECORD> propertyProvider, int rowHeight) {
		Table<RECORD> table = createTable();
		table.setDisplayAsList(true);
		table.setForceFitWidth(true);
		table.setRowHeight(rowHeight);
		table.setHideHeaders(true);
		table.setStripedRows(false);
		TemplateField<RECORD> templateField = new TemplateField<>(template);
		templateField.setPropertyProvider(propertyProvider);
		table.addColumn(new TableColumn<>("data", templateField));
		table.setPropertyExtractor((record, propertyName) -> record);
		return table;
	}

	public InfiniteItemViewModel<RECORD> createInfiniteItemViewModel() {
		InfiniteItemViewModel<RECORD> model = new AbstractInfiniteItemViewModel<>() {
			@Override
			public int getCount() {
				return countRecords;
			}

			@Override
			public List<RECORD> getRecords(int startIndex, int length) {
				return records == null ? Collections.emptyList() : records.stream().skip(startIndex).limit(length).collect(Collectors.toList());
			}
		};
		onDataChanged.addListener(() -> model.onAllDataChanged().fire());
		return model;
	}

	public InfiniteItemView2<RECORD> createItemView2(Template template, float itemWidth, int itemHeight) {
		InfiniteItemView2<RECORD> itemView = new InfiniteItemView2<>(template, itemWidth, itemHeight);
		itemView.setModel(createInfiniteItemViewModel());
		itemView.onItemClicked.addListener(record -> onSelectedRecordChanged.fire(record.getRecord()));
		return itemView;
	}

	public TimeGraphModel createTimeGraphModel(Function<RECORD, Long> recordTimeFunction, String seriesId) {
		StaticRawTimedDataModel delegationModel = new StaticRawTimedDataModel();
		PartitioningTimeGraphModel timeGraphModel = new PartitioningTimeGraphModel(SessionContext.current().getTimeZone(), delegationModel) {
			@Override
			public Interval getDomainX(Collection<String> dataSeriesId) {
				Interval domainX = super.getDomainX(dataSeriesId);
				long diff = (domainX.getMax() - domainX.getMin()) / 20;
				return new Interval(domainX.getMin() - diff, domainX.getMax() + diff);
			}
		};
		onDataChanged.addListener(() -> {
			long[] data = new long[timeGraphRecords.size()];
			for (int i = 0; i < timeGraphRecords.size(); i++) {
				data[i] = recordTimeFunction.apply(timeGraphRecords.get(i));
			}
			delegationModel.setEventTimestampsForDataSeriesIds(seriesId, data);
		});
		return timeGraphModel;
	}

	public TimeGraph createTimeGraph(Function<RECORD, Long> recordTimeFunction, String fieldName) {
		RgbaColor color = Color.MATERIAL_BLUE_700;
		return createTimeGraph(recordTimeFunction, fieldName, color);
	}

	public TimeGraph createTimeGraph(Function<RECORD, Long> recordTimeFunction, String fieldName, RgbaColor color) {
		LineChartLine line = createTimeGraphLine(fieldName, color);
		TimeGraphModel timeGraphModel = createTimeGraphModel(recordTimeFunction, fieldName);
		TimeGraph timeGraph = new TimeGraph(timeGraphModel);
		timeGraph.addLine(line);
		timeGraph.onIntervalSelected.addListener(interval -> {
			setTimeIntervalFilter(interval != null ? new TimeIntervalFilter(fieldName, interval.getMin(), interval.getMax()) : null);
		});
		timeGraph.setSelectedInterval(null);
		return timeGraph;
	}

	private LineChartLine createTimeGraphLine(String fieldName, RgbaColor color) {
		LineChartLine line = new LineChartLine(fieldName, LineChartCurveType.MONOTONE, 0.5f, color, color.withAlpha(0.05f));
		line.setAreaColorScaleMin(color.withAlpha(0.05f));
		line.setAreaColorScaleMax(color.withAlpha(0.5f));
		line.setYScaleType(ScaleType.LINEAR);
		line.setYScaleZoomMode(LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO);
		return line;
	}

	public TreeModel<RECORD> createTreeModel(Function<RECORD, RECORD> parentRecordFunction, Function<RECORD, Boolean> expandedFunction) {
		AbstractTreeModel<RECORD> treeModel = new AbstractTreeModel<>() {
			@Override
			public TreeNodeInfo getTreeNodeInfo(RECORD record) {
				return new TreeNodeInfoImpl<RECORD>(
						parentRecordFunction.apply(record),
						expandedFunction != null ? expandedFunction.apply(record) : false
				);
			}

			@Override
			public List<RECORD> getRecords() {
				return records;
			}
		};
		onDataChanged.addListener((Runnable) treeModel.onAllNodesChanged::fire);
		return treeModel;
	}

	public Tree<RECORD> createTree(Template template, PropertyProvider<RECORD> propertyProvider, Function<RECORD, RECORD> parentRecordFunction, Function<RECORD, Boolean> expandedFunction) {
		Tree<RECORD> tree = new Tree<>(createTreeModel(parentRecordFunction, expandedFunction));
		tree.setEntryTemplate(template);
		tree.setOpenOnSelection(true);
		tree.setPropertyProvider(propertyProvider);
		tree.onNodeSelected.addListener(record -> onSelectedRecordChanged.fire(record));
		return tree;
	}

	public void setFullTextQuery(String query) {
		fullTextQuery = query;
		onDataChanged.fire();
	}

	public void removeFullTextQuery() {
		if (fullTextQuery != null) {
			fullTextQuery = null;
			onDataChanged.fire();
		}
	}

	public void setSorting(String sortField, boolean sortAscending) {
		this.sortField = sortField;
		this.sortAscending = sortAscending;
		onDataChanged.fire();
	}

	public String getSortField() {
		return sortField;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void removeSorting() {
		if (sortField != null) {
			sortField = null;
			onDataChanged.fire();
		}
	}

	public void setTimeIntervalFilter(String fieldName, long start, long end) {
		TimeIntervalFilter timeIntervalFilter = fieldName != null ? new TimeIntervalFilter(fieldName, start, end) : null;
		setTimeIntervalFilter(timeIntervalFilter);
	}

	public void setTimeIntervalFilter(TimeIntervalFilter timeIntervalFilter) {
		this.timeIntervalFilter = timeIntervalFilter;
		onDataChanged.fire();
	}

	public void removeTimeIntervalFilter() {
		if (timeIntervalFilter != null) {
			timeIntervalFilter = null;
			onDataChanged.fire();
		}
	}

	public void setCustomFilter(Predicate<RECORD> customFilter) {
		this.customFilter = customFilter;
		onDataChanged.fire();
	}

	public void removeCustomFilter() {
		this.customFilter = null;
		onDataChanged.fire();
	}

	public void setCustomFieldSorter(Function<String, Comparator<RECORD>> customFieldSorter) {
		this.customFieldSorter = customFieldSorter;
	}

	public void removeCustomFieldSorter() {
		customFieldSorter = null;
	}

	public Function<String, Comparator<RECORD>> getCustomFieldSorter() {
		return customFieldSorter;
	}

	public BiFunction<RECORD, String, Boolean> getCustomFullTextFilter() {
		return customFullTextFilter;
	}

	public void setCustomFullTextFilter(BiFunction<RECORD, String, Boolean> customFullTextFilter) {
		this.customFullTextFilter = customFullTextFilter;
	}

	private Comparator<RECORD> getQuerySorter() {
		if (sortField == null || customFieldSorter == null) {
			return null;
		} else {
			Comparator<RECORD> comparator = customFieldSorter.apply(sortField);
			return (comparator == null || sortAscending) ? comparator : comparator.reversed();
		}
	}

	private void queryRecords() {
		records = performQuery(timeIntervalFilter);
		countRecords = records.size();
		if (timeIntervalFilter != null) {
			timeGraphRecords = performQuery(null);
		} else {
			timeGraphRecords = records;
		}
	}

	private List<RECORD> performQuery(TimeIntervalFilter timeIntervalFilter) {
		List<RECORD> result = queryRecords(fullTextQuery, timeIntervalFilter);
		if (customFilter != null) {
			result = result.stream()
					.filter(record -> customFilter.test(record))
					.collect(Collectors.toList());
		}
		Comparator<RECORD> sorter = getQuerySorter();
		if (sorter != null) {
			result = result.stream().sorted(sorter).collect(Collectors.toList());
		}
		return result;
	}

	public abstract List<RECORD> queryRecords(String fullTextQuery, TimeIntervalFilter timeIntervalFilter);

}
