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
package org.teamapps.application.api.application.form;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.event.TwoWayBindableValueFireAlways;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.StandardPrivilegeGroup;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.form.FormButtonUtils;
import org.teamapps.application.ux.form.FormMetaFieldsImpl;
import org.teamapps.application.ux.view.RecordVersionsView;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.dialogue.Dialogue;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.Fields;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractLazyRenderingFormView<ENTITY extends Entity<?>> extends AbstractLazyRenderingApplicationView {

	private ResponsiveForm<ENTITY> form;
	private ResponsiveFormLayout formLayout;
	public final Event<Void> onRevertRequested = new Event<>();
	public final Event<Void> onEntityChanged = new Event<>();
	private final TwoWayBindableValue<ENTITY> selectedEntity = TwoWayBindableValueFireAlways.create();
	private final TwoWayBindableValue<Boolean> dataModified = TwoWayBindableValue.create();
	private final TwoWayBindableValue<FormEditState> formEditState = TwoWayBindableValue.create();

	private ToolbarButton newButton;
	private ToolbarButton editButton;
	private ToolbarButton saveButton;
	private ToolbarButton revertButton;
	private ToolbarButton deleteButton;
	private ToolbarButton restoreButton;
	private ToolbarButton versionsButton;

	private Function<ENTITY, Boolean> saveHandler;
	private Supplier<Boolean> validationHandler;
	private BiConsumer<FormEditState, List<AbstractField<?>>> formEditStateHandler;
	private StandardPrivilegeGroup standardPrivilegeGroup;
	private OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private Function<ENTITY, OrganizationUnitView> entityOrganizationUnitSelector;
	private List<AbstractField<?>> editableFields = new ArrayList<>();

	private FormMetaFieldsImpl metaFields;

	public AbstractLazyRenderingFormView(ApplicationInstanceData applicationInstanceData) {
		this(new ResponsiveForm<>(120, 200, 0), applicationInstanceData);
	}

	public AbstractLazyRenderingFormView(ResponsiveForm<ENTITY> form, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		this.form = form;
	}

	@Override
	public AbstractLazyRenderingFormView<ENTITY> setParentView(View parent) {
		super.setParentView(parent);
		return this;
	}

	@Override
	public AbstractLazyRenderingFormView<ENTITY> setParentWindow(Window parent) {
		super.setParentWindow(parent);
		return this;
	}

	@Override
	public AbstractLazyRenderingFormView<ENTITY> setParentPanel(Panel parent) {
		super.setParentPanel(parent);
		return this;
	}

	@Override
	public void createUi() {
		metaFields = new FormMetaFieldsImpl(getApplicationInstanceData());
		formLayout = form.addResponsiveFormLayout(450);
		newButton = FormButtonUtils.createNewButton(getApplicationInstanceData());
		editButton = FormButtonUtils.createEditButton(getApplicationInstanceData());
		saveButton = FormButtonUtils.createSaveButton(getApplicationInstanceData());
		revertButton = FormButtonUtils.createRevertButton(getApplicationInstanceData());
		deleteButton = FormButtonUtils.createDeleteButton(getApplicationInstanceData());
		restoreButton = FormButtonUtils.createRestoreButton(getApplicationInstanceData());
		versionsButton = ToolbarButton.createSmall(ApplicationIcons.CLOCK_BACK, getApplicationInstanceData().getLocalized(Dictionary.SHOW_MODIFICATION_HISTORY));

		newButton.setVisible(isEntityCreationAllowed());
		editButton.setVisible(false);
		saveButton.setVisible(false);
		revertButton.setVisible(false);
		deleteButton.setVisible(false);
		restoreButton.setVisible(false);
		versionsButton.setVisible(false);

		ToolbarButtonGroup buttonGroup = createToolbarButtonGroup(true);
		buttonGroup.addButton(newButton);
		buttonGroup.addButton(saveButton);

		buttonGroup = createToolbarButtonGroup(true);
		buttonGroup.addButton(editButton);

		buttonGroup = createToolbarButtonGroup(true);
		buttonGroup.addButton(revertButton);

		buttonGroup = createToolbarButtonGroup(true);
		buttonGroup.addButton(deleteButton);
		buttonGroup.addButton(restoreButton);

		buttonGroup = createToolbarButtonGroup(true);
		buttonGroup.addButton(versionsButton);


		newButton.onClick.addListener(() -> {
			if (isEntityCreationAllowed()) {
				dataModified.set(false);
				ENTITY entity = createNewEntity();
				selectedEntity.set(entity);
				if (formEditState.get() == FormEditState.READ_STATE) {
					formEditState.set(FormEditState.EDIT_STATE);
				}
				newButton.setVisible(false);
			}
		});

		editButton.onClick.addListener(() -> {
			if (formEditState.get() == FormEditState.READ_STATE) {
				formEditState.set(FormEditState.EDIT_STATE);
			}
		});

		saveButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (isEntityEditable(entity) && Fields.validateAll(form.getFields()) && validationHandler.get()) {
				if (saveHandler != null) {
					if (!saveHandler.apply(entity)) {
						return;
					}
					saveButton.setVisible(false);
					restoreButton.setVisible(false);
					newButton.setVisible(isEntityCreationAllowed());
					markAllFieldsUnchanged();
					UiUtils.showSaveNotification(true, getApplicationInstanceData());
					onEntityChanged.fire();
					if (formEditState.get() == FormEditState.EDIT_STATE) {
						formEditState.set(FormEditState.READ_STATE);
					}
				}
			} else {
				UiUtils.showSaveNotification(false, getApplicationInstanceData());
			}
		});

		revertButton.onClick.addListener(() -> {
			selectedEntity.set(selectedEntity.get());
			saveButton.setVisible(false);
			revertButton.setVisible(false);
			newButton.setVisible(isEntityCreationAllowed());
			deleteButton.setVisible(isEntityDeletionAllowed(selectedEntity.get()));
			markAllFieldsUnchanged();
			onRevertRequested.fire();
		});

		deleteButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (isEntityDeletionAllowed(entity)) {
				Dialogue.showOkCancel(ApplicationIcons.DELETE, getApplicationInstanceData().getLocalized(Dictionary.DELETE_RECORD), getApplicationInstanceData().getLocalized(Dictionary.SENTENCE_DO_YOU_REALLY_WANT_TO_DELETE_THE_RE__)).addListener(result -> {
					if (result) {
						entity.delete();
						deleteButton.setVisible(false);
						restoreButton.setVisible(isEntityRestoreAllowed(entity));
						onEntityChanged.fire();
					}
				});
			}
		});

		restoreButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (isEntityRestoreAllowed(entity)) {
				entity.restoreDeleted();
				restoreButton.setVisible(false);
				deleteButton.setVisible(isEntityDeletionAllowed(selectedEntity.get()));
				onEntityChanged.fire();
			}
		});

		versionsButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			RecordVersionsView<ENTITY> recordVersionsView = new RecordVersionsView<>(entity, getApplicationInstanceData());
			AbstractUdbEntity<ENTITY> udbEntity = (AbstractUdbEntity<ENTITY>) entity;
			udbEntity.getTableIndex().getFieldIndices().forEach(col -> recordVersionsView.addField(col.getName(), null));
			recordVersionsView.showVersionsWindow();
		});

		formEditState.onChanged().addListener(state -> {
			if (formEditStateHandler != null) {
				ENTITY entity = selectedEntity.get();
				List<AbstractField<?>> fields = !editableFields.isEmpty() ? editableFields : form.getFields();
				formEditStateHandler.accept(state, fields);
			}
		});

		selectedEntity.onChanged().addListener(entity -> {
			newButton.setVisible(isEntityCreationAllowed());
			deleteButton.setVisible(isEntityDeletionAllowed(entity));
			restoreButton.setVisible(isEntityRestoreAllowed(entity));
			editButton.setVisible(false);
			revertButton.setVisible(false);
			restoreButton.setVisible(false);
			versionsButton.setVisible(false);
			if (entity.isStored() && isEntityEditable(entity)) {
				editButton.setVisible(true);
				versionsButton.setVisible(true);
			}
			metaFields.updateEntity(entity);
			clearMessages();
			markAllFieldsUnchanged();
		});

		createForm(formLayout);
		form.getFields().forEach(field -> {
			field.onValueChanged.addListener(() -> {
				dataModified.set(true);
				revertButton.setVisible(true);
				markFieldChanged(field);
				if (isEntityEditable(selectedEntity.get())) {
					saveButton.setVisible(true);
				}
			});
			if (field instanceof TextField) {
				TextField textField = (TextField) field;
				textField.onTextInput.addListener(() -> dataModified.set(true));
				revertButton.setVisible(true);
				if (!saveButton.isVisible()) {
					if (isEntityEditable(selectedEntity.get())) {
						saveButton.setVisible(true);
					}
				}
			}
		});
	}

	public void addMetaFieldsSection() {
		metaFields.addMetaFields(formLayout, false);
	}

	public FormMetaFields getMetaFields() {
		return metaFields;
	}

	public void setSaveHandler(Function<ENTITY, Boolean> saveHandler) {
		this.saveHandler = saveHandler;
	}

	public void setSelectedEntity(ENTITY entity) {
		selectedEntity.set(entity);
	}

	public void setStandardPrivilegeGroup(StandardPrivilegeGroup standardPrivilegeGroup) {
		this.standardPrivilegeGroup = standardPrivilegeGroup;
	}

	public void setOrganizationalPrivilegeGroup(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> entityOrganizationUnitSelector) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.entityOrganizationUnitSelector = entityOrganizationUnitSelector;
	}

	public void setValidationHandler(Supplier<Boolean> validationHandler) {
		this.validationHandler = validationHandler;
	}

	public void setFormEditStateHandler(BiConsumer<FormEditState, List<AbstractField<?>>> formEditStateHandler) {
		this.formEditStateHandler = formEditStateHandler;
	}

	public void setFormEditState(FormEditState formEditState) {
		this.formEditState.set(formEditState);
	}

	public void setEditableFields(List<AbstractField<?>> fields) {
		this.editableFields = fields;
	}

	public void addEditableField(AbstractField<?> field) {
		editableFields.add(field);
	}

	public void addEditableFields(AbstractField<?>... fields) {
		editableFields.addAll(Arrays.stream(fields).toList());
	}

	public ObservableValue<Boolean> getDataModifiedObservable() {
		return dataModified;
	}

	public Event<Void> getOnRevertRequested() {
		return onRevertRequested;
	}

	public void handleSelectedEntityChanged(ENTITY entity) {
		selectedEntity.set(entity);
	}

	public ObservableValue<FormEditState> getFormEditState() {
		return formEditState;
	}


	public abstract void createForm(ResponsiveFormLayout formLayout);


	public abstract ENTITY createNewEntity();


	@Override
	public Component getViewComponent() {
		return form;
	}

	protected void markAllFieldsUnchanged() {
		form.getFields().forEach(field -> {
			field.setCssStyle(".field-border", "border-color", null);
			field.setCssStyle(".field-border-glow", "box-shadow", null);
			field.setValueChangedByClient(false);
		});
	}

	protected void markFieldChanged(AbstractField<?> field) {
		field.setCssStyle(".field-border", "border-color", "#ec9a1a");
		field.setCssStyle(".field-border-glow", "box-shadow", "0 0 3px 0 #ec9a1a");
	}

	public void clearMessages() {
		form.getFields().forEach(AbstractField::clearValidatorMessages);
	}

	public boolean isEntityEditable(ENTITY entity) {
		if (entity == null) {
			return false;
		}
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		if (standardPrivilegeGroup != null) {
			return getApplicationInstanceData().isAllowed(standardPrivilegeGroup, privilege);
		} else {
			OrganizationUnitView selectedOrganizationUnit = entityOrganizationUnitSelector.apply(entity);
			if (selectedOrganizationUnit != null) {
				return getApplicationInstanceData().isAllowed(organizationalPrivilegeGroup, privilege, selectedOrganizationUnit);
			} else {
				return !getApplicationInstanceData().getAllowedUnits(organizationalPrivilegeGroup, privilege).isEmpty();
			}
		}
	}

	public boolean isEntityDeletionAllowed(ENTITY entity) {
		if (entity == null || !entity.isStored() || entity.isDeleted()) {
			return false;
		}
		Privilege privilege = Privilege.DELETE;
		if (standardPrivilegeGroup != null) {
			return getApplicationInstanceData().isAllowed(standardPrivilegeGroup, privilege);
		} else {
			OrganizationUnitView selectedOrganizationUnit = entityOrganizationUnitSelector.apply(entity);
			if (selectedOrganizationUnit != null) {
				return getApplicationInstanceData().isAllowed(organizationalPrivilegeGroup, privilege, entityOrganizationUnitSelector.apply(entity));
			} else {
				return !getApplicationInstanceData().getAllowedUnits(organizationalPrivilegeGroup, privilege).isEmpty();
			}
		}
	}

	public boolean isEntityRestoreAllowed(ENTITY entity) {
		if (entity == null || !entity.isDeleted() || !entity.isRestorable()) {
			return false;
		}
		Privilege privilege = Privilege.RESTORE;
		if (standardPrivilegeGroup != null) {
			return getApplicationInstanceData().isAllowed(standardPrivilegeGroup, privilege);
		} else {
			return getApplicationInstanceData().isAllowed(organizationalPrivilegeGroup, privilege, entityOrganizationUnitSelector.apply(entity));
		}
	}

	public boolean isEntityCreationAllowed() {
		return standardPrivilegeGroup != null ? getApplicationInstanceData().isAllowed(standardPrivilegeGroup, Privilege.CREATE) : !getApplicationInstanceData().getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
	}
}
