package org.teamapps.application.ux.view;

import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.template.BaseTemplateRecord;

import java.util.function.Function;

public class RecordVersionViewFieldData {
	private String fieldName;
	private String fieldTitle;
	private Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord;
	private boolean customField;
	private AbstractField<?> formField;
	private Function<Object, Object> formFieldDataProvider;
	private AbstractField<?> tableField;
	private Function<Object, Object> tableFieldDataProvider;

	private int tableColumnWidth = 200;

	public RecordVersionViewFieldData(String fieldName, String fieldTitle) {
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
	}

	public RecordVersionViewFieldData(String fieldName, String fieldTitle, Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord) {
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.referencedRecordIdToTemplateRecord = referencedRecordIdToTemplateRecord;
	}

	public RecordVersionViewFieldData(String fieldName, String fieldTitle, AbstractField<?> formField, Function<Object, Object> formFieldDataProvider, AbstractField<?> tableField, Function<Object, Object> tableFieldDataProvider) {
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.formField = formField;
		this.formFieldDataProvider = formFieldDataProvider;
		this.tableField = tableField;
		this.tableFieldDataProvider = tableFieldDataProvider;
		this.customField = true;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldTitle() {
		return fieldTitle;
	}

	public Function<Integer, BaseTemplateRecord<Integer>> getReferencedRecordIdToTemplateRecord() {
		return referencedRecordIdToTemplateRecord;
	}

	public boolean isCustomField() {
		return customField;
	}

	public AbstractField getFormField() {
		return formField;
	}

	public Function<Object, Object> getFormFieldDataProvider() {
		return formFieldDataProvider;
	}

	public AbstractField getTableField() {
		return tableField;
	}

	public Function<Object, Object> getTableFieldDataProvider() {
		return tableFieldDataProvider;
	}

	public int getTableColumnWidth() {
		return tableColumnWidth;
	}

	public void setTableColumnWidth(int tableColumnWidth) {
		this.tableColumnWidth = tableColumnWidth;
	}
}
