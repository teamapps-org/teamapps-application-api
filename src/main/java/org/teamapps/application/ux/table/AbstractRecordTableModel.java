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
package org.teamapps.application.ux.table;

import org.teamapps.data.value.Sorting;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.record.ItemRange;
import org.teamapps.ux.component.infiniteitemview.RecordsAddedEvent;
import org.teamapps.ux.component.infiniteitemview.RecordsChangedEvent;
import org.teamapps.ux.component.infiniteitemview.RecordsRemovedEvent;
import org.teamapps.ux.component.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractRecordTableModel<RECORD> extends AbstractTableModel<RECORD> {

	private List<RECORD> records;
	private String fullTextFilter;
	private Sorting sorting;
	public final Event<String> onFullTextFilterChanged = new Event<>();
	public final Event<Sorting> onSortingChanged = new Event<>();

	public synchronized void handleRecordAdded(RECORD record) {
		if (sorting == null) {
			int recordPosition = records.size();
			records.add(record);
			this.onRecordAdded.fire(new RecordsAddedEvent<>(recordPosition, Collections.singletonList(record)));
		} else {
			refresh();
		}
	}

	public synchronized void handleRecordChanged(RECORD record) {
		int recordPosition = findRecordPosition(record);
		if (recordPosition >= 0) {
			this.onRecordsChanged().fire(new RecordsChangedEvent<>(recordPosition, Collections.singletonList(record)));
		}
	}

	public synchronized void handleRecordRemoved(RECORD record) {
		int recordPosition = findRecordPosition(record);
		if (recordPosition >= 0) {
			this.onRecordsRemoved().fire(new RecordsRemovedEvent<>(ItemRange.startLength(recordPosition, 1)));
		}
	}

	private int findRecordPosition(RECORD record) {
		checkInitialized();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).equals(record)) {
				return i;
			}
		}
		return -1;
	}

	public synchronized RECORD getNextRecord(RECORD record) {
		int recordPosition = findRecordPosition(record);
		if (recordPosition >= 0 && recordPosition + 1 < records.size()) {
			return records.get(recordPosition + 1);
		} else {
			return null;
		}
	}

	public synchronized RECORD getPreviousRecord(RECORD record) {
		int recordPosition = findRecordPosition(record);
		if (recordPosition > 0) {
			return records.get(recordPosition - 1);
		} else {
			return null;
		}
	}


	public void setFullTextFilter(String fullTextFilter) {
		if (!Objects.equals(fullTextFilter, this.fullTextFilter)) {
			this.fullTextFilter = fullTextFilter;
			runQuery();
			onFullTextFilterChanged.fire(fullTextFilter);
			onAllDataChanged().fire();
		}
	}

	public String getFullTextFilter() {
		return fullTextFilter;
	}

	@Override
	public void setSorting(Sorting sorting) {
		if (!Objects.equals(this.sorting, sorting)) {
			this.sorting = sorting;
			runQuery();
			onSortingChanged.fire(sorting);
			onAllDataChanged().fire();
		}
	}

	public Sorting getSorting() {
		return sorting;
	}

	public String getFulltextFilter() {
		return fullTextFilter;
	}

	@Override
	public int getCount() {
		checkInitialized();
		return records.size();
	}

	@Override
	public List<RECORD> getRecords(int startIndex, int length) {
		checkInitialized();
		if (startIndex >= records.size()) {
			return Collections.emptyList();
		}
		if (startIndex + length > records.size()) {
			length = records.size() - startIndex;
		}
		return records.subList(startIndex, startIndex + length);
	}

	private void checkInitialized() {
		if (records == null) {
			runQuery();
		}
	}

	public void refresh() {
		runQuery();
		onAllDataChanged().fire();
	}

	private synchronized void runQuery() {
		this.records = new ArrayList<>(executeQuery(fullTextFilter, sorting));
	}

	public abstract List<RECORD> executeQuery(String fullTextSearchString, Sorting sorting);
}
