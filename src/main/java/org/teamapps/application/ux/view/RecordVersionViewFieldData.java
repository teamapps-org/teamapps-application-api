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
package org.teamapps.application.ux.view;

import org.teamapps.universaldb.index.FieldIndex;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.template.Template;

import java.util.function.Function;

public class RecordVersionViewFieldData {
	private final FieldIndex fieldIndex;
	private final String fieldName;
	private final String fieldTitle;
	private Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord;
	private boolean customField;
	private AbstractField<?> formField;
	private Function<Object, Object> formFieldDataProvider;
	private AbstractField<?> tableField;
	private Function<Object, Object> tableFieldDataProvider;
	private Template template;

	private int tableColumnWidth = 200;

	public RecordVersionViewFieldData(FieldIndex fieldIndex, String fieldName, String fieldTitle) {
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
	}

	public RecordVersionViewFieldData(FieldIndex fieldIndex, String fieldName, String fieldTitle, Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord) {
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.referencedRecordIdToTemplateRecord = referencedRecordIdToTemplateRecord;
	}

	public RecordVersionViewFieldData(FieldIndex fieldIndex, String fieldName, String fieldTitle, Function<Integer, BaseTemplateRecord<Integer>> referencedRecordIdToTemplateRecord, Template template) {
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.referencedRecordIdToTemplateRecord = referencedRecordIdToTemplateRecord;
		this.template = template;
	}

	public RecordVersionViewFieldData(FieldIndex fieldIndex, String fieldName, String fieldTitle, AbstractField<?> formField, Function<Object, Object> formFieldDataProvider, AbstractField<?> tableField, Function<Object, Object> tableFieldDataProvider) {
		this.fieldIndex = fieldIndex;
		this.fieldName = fieldName;
		this.fieldTitle = fieldTitle;
		this.formField = formField;
		this.formFieldDataProvider = formFieldDataProvider;
		this.tableField = tableField;
		this.tableFieldDataProvider = tableFieldDataProvider;
		this.customField = true;
	}

	public FieldIndex getFieldIndex() {
		return fieldIndex;
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

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}
}
