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
package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.index.ColumnIndex;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.numeric.IntegerIndex;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.datetime.InstantDateTimeField;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.format.SizingPolicy;

public class FormMetaFieldsImpl implements FormMetaFields {

	private final ApplicationInstanceData applicationInstanceData;
	private final TemplateField<Integer> createdByField;
	private final TemplateField<Integer> modifiedByField;
	private final InstantDateTimeField creationDateField;
	private final InstantDateTimeField modificationDateField;

	public FormMetaFieldsImpl(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;

		createdByField = applicationInstanceData.getComponentFactory().createUserTemplateField();
		modifiedByField = applicationInstanceData.getComponentFactory().createUserTemplateField();
		creationDateField = new InstantDateTimeField();
		modificationDateField = new InstantDateTimeField();
		creationDateField.setEditingMode(FieldEditingMode.READONLY);
		modificationDateField.setEditingMode(FieldEditingMode.READONLY);
	}

	@Override
	public ResponsiveFormSection addMetaFields(ResponsiveFormLayout formLayout, boolean withIcons) {
		Icon dateIcon = withIcons ? ApplicationIcons.CALENDAR_CLOCK : null;
		Icon userIcon = withIcons ? ApplicationIcons.USER : null;
		ResponsiveFormSection formSection = formLayout.addSection(dateIcon, applicationInstanceData.getLocalized(Dictionary.META_DATA));
		formLayout.addLabelAndField(userIcon, applicationInstanceData.getLocalized(Dictionary.CREATION), creationDateField).field.getColumnDefinition().setWidthPolicy(SizingPolicy.AUTO);
		formLayout.addLabelAndField(null, null, createdByField, false).field.getColumnDefinition().setWidthPolicy(SizingPolicy.FRACTION);
		formLayout.addLabelAndField(dateIcon, applicationInstanceData.getLocalized(Dictionary.MODIFICATION), modificationDateField);
		formLayout.addLabelAndField(null, null, modifiedByField, false);
		return formSection;
	}

	@Override
	public void updateEntity(Entity<?> entity) {
		if (entity instanceof AbstractUdbEntity) {
			AbstractUdbEntity<?> udbEntity = (AbstractUdbEntity<?>) entity;
			TableIndex tableIndex = udbEntity.getTableIndex();
			ColumnIndex createdByIndex = tableIndex.getColumnIndex(Table.FIELD_CREATED_BY);
			createdByField.setValue(createdByIndex != null ? udbEntity.getIntValue((IntegerIndex) createdByIndex) : 0);
			ColumnIndex modifiedByIndex = tableIndex.getColumnIndex(Table.FIELD_MODIFIED_BY);
			modifiedByField.setValue(modifiedByIndex != null ? udbEntity.getIntValue((IntegerIndex) modifiedByIndex) : 0);
			ColumnIndex creationDateIndex = tableIndex.getColumnIndex(Table.FIELD_CREATION_DATE);
			creationDateField.setValue(creationDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) creationDateIndex) : null);
			ColumnIndex modificationDateIndex = tableIndex.getColumnIndex(Table.FIELD_MODIFICATION_DATE);
			modificationDateField.setValue(modificationDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) modificationDateIndex) : null);
			createdByField.setVisible(createdByField.getValue() != 0);
			modifiedByField.setVisible(modifiedByField.getValue() != 0);
			creationDateField.setVisible(creationDateField.getValue() != null);
			modificationDateField.setVisible(modificationDateField.getValue() != null && !modificationDateField.getValue().equals(creationDateField.getValue()));
		}
	}
}
