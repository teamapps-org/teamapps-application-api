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
package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.privilege.SimplePrivilegeImpl;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.org.OrganizationViewUtils;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.model.controlcenter.UserView;
import org.teamapps.universaldb.index.FieldIndex;
import org.teamapps.universaldb.index.TableIndex;
import org.teamapps.universaldb.index.numeric.IntegerIndex;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.schema.Table;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.field.datetime.InstantDateTimeField;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.session.DateTimeFormatDescriptor;

public class FormMetaFieldsImpl implements FormMetaFields {

	private final ApplicationInstanceData applicationInstanceData;
	private final TemplateField<Integer> createdByField;
	private final TemplateField<Integer> modifiedByField;
	private final TemplateField<Integer> deletedByField;
	private final TemplateField<Integer> restoredByField;
	private final InstantDateTimeField creationDateField;
	private final InstantDateTimeField modificationDateField;
	private final InstantDateTimeField deletionDateField;
	private final InstantDateTimeField restoreDateField;
	private final TextField idField;
	private final Event<Integer> onClicked = new Event<>();

	public FormMetaFieldsImpl(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;

		createdByField = applicationInstanceData.getComponentFactory().createUserTemplateField();
		modifiedByField = applicationInstanceData.getComponentFactory().createUserTemplateField();
		deletedByField = applicationInstanceData.getComponentFactory().createUserTemplateField();
		restoredByField = applicationInstanceData.getComponentFactory().createUserTemplateField();

		creationDateField = new InstantDateTimeField();
		modificationDateField = new InstantDateTimeField();
		deletionDateField = new InstantDateTimeField();
		restoreDateField = new InstantDateTimeField();

		creationDateField.setDateFormat(DateTimeFormatDescriptor.forDate(DateTimeFormatDescriptor.FullLongMediumShortType.LONG));
		modificationDateField.setDateFormat(DateTimeFormatDescriptor.forDate(DateTimeFormatDescriptor.FullLongMediumShortType.LONG));
		deletionDateField.setDateFormat(DateTimeFormatDescriptor.forDate(DateTimeFormatDescriptor.FullLongMediumShortType.LONG));
		restoreDateField.setDateFormat(DateTimeFormatDescriptor.forDate(DateTimeFormatDescriptor.FullLongMediumShortType.LONG));

		idField = new TextField();

		creationDateField.setEditingMode(FieldEditingMode.READONLY);
		modificationDateField.setEditingMode(FieldEditingMode.READONLY);
		deletionDateField.setEditingMode(FieldEditingMode.READONLY);
		restoreDateField.setEditingMode(FieldEditingMode.READONLY);
		idField.setEditingMode(FieldEditingMode.READONLY);

		createdByField.setVisible(false);
		modifiedByField.setVisible(false);
		deletedByField.setVisible(false);
		restoredByField.setVisible(false);
		creationDateField.setVisible(false);
		modificationDateField.setVisible(false);
		deletionDateField.setVisible(false);
		restoreDateField.setVisible(false);
		idField.setVisible(false);

		createdByField.onClicked.addListener(() -> onClicked.fire(createdByField.getValue()));
		modifiedByField.onClicked.addListener(() -> onClicked.fire(modifiedByField.getValue()));
		deletedByField.onClicked.addListener(() -> onClicked.fire(deletedByField.getValue()));
		restoredByField.onClicked.addListener(() -> onClicked.fire(restoredByField.getValue()));

		onClicked.addListener(id -> {
			if (id > 0) {
				UserView userView = UserView.getById(id);
				FormWindow formWindow = new FormWindow(ApplicationIcons.USER, userView.getFirstName() + " " + userView.getLastName(), applicationInstanceData);
				formWindow.getWindow().setWidth(550);
				formWindow.addOkButton().onClick.addListener(formWindow::close);
				TemplateField<Integer> userTemplateField = applicationInstanceData.getComponentFactory().createUserTemplateField();
				userTemplateField.setTemplate(BaseTemplate.LIST_ITEM_LARGE_ICON_TWO_LINES);
				userTemplateField.setValue(id);
				TextField idField = new TextField();
				idField.setValue(id + "");
				idField.setEditingMode(FieldEditingMode.READONLY);
				OrganizationUnitView unit = userView.getOrganizationUnit();
				TemplateField<OrganizationUnitView> unitViewTemplateField = UiUtils.createTemplateField(BaseTemplate.LIST_ITEM_LARGE_ICON_TWO_LINES, OrganizationViewUtils.creatOrganizationUnitWithPathPropertyProvider(applicationInstanceData));
				unitViewTemplateField.setValue(unit);
				formWindow.addSection();
				formWindow.addField(applicationInstanceData.getLocalized(Dictionary.USER_ID), idField);
				formWindow.addField(applicationInstanceData.getLocalized(Dictionary.USER_NAME), userTemplateField);
				formWindow.addField(applicationInstanceData.getLocalized(Dictionary.ORGANIZATION), unitViewTemplateField);
				formWindow.show();
			}
		});
	}

	@Override
	public ResponsiveFormSection addMetaFields(ResponsiveFormLayout formLayout, boolean withIcons, Event<? extends Entity<?>> onEntityChange) {
		onEntityChange.addListener(this::updateEntity);
		return addMetaFields(formLayout, withIcons);
	}

	@Override
	public ResponsiveFormSection addMetaFields(ResponsiveFormLayout formLayout, boolean withIcons) {
		Icon dateIcon = withIcons ? ApplicationIcons.CALENDAR_CLOCK : null;
		Icon userIcon = withIcons ? ApplicationIcons.USER : null;
		ResponsiveFormSection formSection = formLayout.addSection(ApplicationIcons.WINDOW_SIDEBAR, applicationInstanceData.getLocalized(Dictionary.META_DATA)).setCollapsed(false);
		formSection.setHideWhenNoVisibleFields(true);
		formLayout.addLabelAndField(userIcon, applicationInstanceData.getLocalized(Dictionary.CREATION), creationDateField).field.getColumnDefinition().setWidthPolicy(SizingPolicy.AUTO);
		formLayout.addLabelAndField(null, null, createdByField, false).field.getColumnDefinition().setWidthPolicy(SizingPolicy.FRACTION);
		formLayout.addLabelAndField(dateIcon, applicationInstanceData.getLocalized(Dictionary.MODIFICATION), modificationDateField);
		formLayout.addLabelAndField(null, null, modifiedByField, false);
		formLayout.addLabelAndField(dateIcon, applicationInstanceData.getLocalized(Dictionary.DELETED), deletionDateField);
		formLayout.addLabelAndField(null, null, deletedByField, false);
		formLayout.addLabelAndField(dateIcon, applicationInstanceData.getLocalized(Dictionary.RESTORED), restoreDateField);
		formLayout.addLabelAndField(null, null, restoredByField, false);
		if (applicationInstanceData.isAllowed(new SimplePrivilegeImpl("any", null, null, null))) {
			formLayout.addLabelAndField(null, "ID", idField);
		}
		return formSection;
	}

	@Override
	public void updateEntity(Entity<?> entity) {
		if (entity instanceof AbstractUdbEntity) {
			AbstractUdbEntity<?> udbEntity = (AbstractUdbEntity<?>) entity;
			TableIndex tableIndex = udbEntity.getTableIndex();
			FieldIndex<?, ?> createdByIndex = tableIndex.getFieldIndex(Table.FIELD_CREATED_BY);
			FieldIndex<?, ?> modifiedByIndex = tableIndex.getFieldIndex(Table.FIELD_MODIFIED_BY);
			FieldIndex<?, ?> deletedByIndex = tableIndex.getFieldIndex(Table.FIELD_DELETED_BY);
			FieldIndex<?, ?> restoredByIndex = tableIndex.getFieldIndex(Table.FIELD_RESTORED_BY);
			FieldIndex<?, ?> creationDateIndex = tableIndex.getFieldIndex(Table.FIELD_CREATION_DATE);
			FieldIndex<?, ?> modificationDateIndex = tableIndex.getFieldIndex(Table.FIELD_MODIFICATION_DATE);
			FieldIndex<?, ?> deletionDateIndex = tableIndex.getFieldIndex(Table.FIELD_DELETION_DATE);
			FieldIndex<?, ?> restoreDateIndex = tableIndex.getFieldIndex(Table.FIELD_RESTORE_DATE);

			createdByField.setValue(createdByIndex != null ? udbEntity.getIntValue((IntegerIndex) createdByIndex) : 0);
			modifiedByField.setValue(modifiedByIndex != null ? udbEntity.getIntValue((IntegerIndex) modifiedByIndex) : 0);
			deletedByField.setValue(deletedByIndex != null ? udbEntity.getIntValue((IntegerIndex) deletedByIndex) : 0);
			restoredByField.setValue(restoredByIndex != null ? udbEntity.getIntValue((IntegerIndex) restoredByIndex) : 0);

			creationDateField.setValue(creationDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) creationDateIndex) : null);
			modificationDateField.setValue(modificationDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) modificationDateIndex) : null);
			deletionDateField.setValue(deletionDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) deletionDateIndex) : null);
			restoreDateField.setValue(restoreDateIndex != null ? udbEntity.getTimestampValue((IntegerIndex) restoreDateIndex) : null);

			boolean showModification = modificationDateField.getValue() != null && !modificationDateField.getValue().equals(creationDateField.getValue());
			createdByField.setVisible(createdByField.getValue() != 0);
			modifiedByField.setVisible(showModification);
			deletedByField.setVisible(deletedByField.getValue() != 0);
			restoredByField.setVisible(restoredByField.getValue() != 0);
			creationDateField.setVisible(creationDateField.getValue() != null);
			modificationDateField.setVisible(showModification);
			deletionDateField.setVisible(deletedByField.getValue() != 0);
			restoreDateField.setVisible(restoredByField.getValue() != 0);
			idField.setValue("" + entity.getId());
			idField.setVisible(entity.getId() != 0);
		}
	}
}
