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
