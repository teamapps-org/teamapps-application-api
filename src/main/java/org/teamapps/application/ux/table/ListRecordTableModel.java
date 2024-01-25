/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
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

import java.util.Collections;
import java.util.List;

public class ListRecordTableModel<RECORD> extends AbstractRecordTableModel<RECORD> {

	private final List<RECORD> records;

	public ListRecordTableModel() {
		this(Collections.emptyList());
	}

	public ListRecordTableModel(List<RECORD> records) {
		this.records = records;
	}

	public void setRecords(List<RECORD> records) {
		this.records.clear();
		this.records.addAll(records);
		refresh();
	}

	public void addRecord(RECORD record) {
		records.add(record);
		handleRecordAdded(record);
	}

	public void removeRecord(RECORD record) {
		records.remove(record);
		handleRecordRemoved(record);
	}

	public List<RECORD> getRecords() {
		return records;
	}

	@Override
	public List<RECORD> executeQuery(String fullTextSearchString, Sorting sorting) {
		return records;
	}
}
