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
package org.teamapps.application.ui.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.ApplicationInstanceDataMethods;
import org.teamapps.application.api.application.entity.EntityUpdateType;
import org.teamapps.application.api.event.TwoWayBindableValueFireAlways;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.application.ui.changehistory.EntityChangeHistoryView;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.application.ui.model.ValidationMessage;
import org.teamapps.application.ui.notification.NotificationUpdateController;
import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.form.FormPanel;
import org.teamapps.application.ux.view.RecordVersionsView;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.model.TableModel;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.dialogue.FormDialogue;
import org.teamapps.ux.component.field.*;
import org.teamapps.ux.component.field.upload.FileField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.table.ListTable;
import org.teamapps.ux.component.table.ListTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.util.*;
import java.util.function.*;

public class FormControllerImpl<ENTITY extends Entity<ENTITY>> implements FormController<ENTITY>, ApplicationInstanceDataMethods {

	private final TwoWayBindableValue<ENTITY> selectedFormEntity = TwoWayBindableValueFireAlways.create();
	private final TwoWayBindableValue<ENTITY> synchronizedEditsEntityCopy = TwoWayBindableValue.create();
	private final ObservableValue<Boolean> visibilityProvider;
	private final FormButtonSize buttonSize;
	private final Function<FormButton<ENTITY>, ToolbarButtonGroup> toolbarButtonGroupFunction;
	private final LifecycleEntityModel<ENTITY> lifecycleEntityModel;
	private final EntityPrivileges<ENTITY> entityPrivileges;
	private final ApplicationInstanceData applicationInstanceData;

	private final List<FormButton<ENTITY>> buttons = new ArrayList<>();
	private final Map<FormButton<ENTITY>, ToolbarButton> buttonMap = new HashMap<>();
	private final Map<FormButton<ENTITY>, SimpleItemView<FormButton<ENTITY>>> itemViewMap = new HashMap<>();
	private final List<AbstractField<?>> formFields = new ArrayList<>();
	private final Set<AbstractField<?>> initiallyVisibleFields = new HashSet<>();
	private final TwoWayBindableValue<FormEditMode> formEditMode = TwoWayBindableValueFireAlways.create(FormEditMode.COMBINED_EDIT_AND_READ);
	private final TwoWayBindableValue<FormEntityState> formEntityState = TwoWayBindableValue.create(FormEntityState.NOTHING);
	private final Map<String, ToolbarButtonGroup> toolbarButtonGroupMap = new HashMap<>();
	private final Map<AbstractField<?>, Consumer<ENTITY>> fieldToEntityHandlerMap = new HashMap<>();

	private boolean blockNextChangeEvent = false;
	private ResponsiveForm<ENTITY> form;
	private ResponsiveFormLayout formLayout;
	private ENTITY currentEntity;
//	private FormEntityState entityState = FormEntityState.NOTHING;

	private Function<ENTITY, Boolean> formToEntityFunction;
	private BiConsumer<ENTITY, Consumer<Boolean>> entitySelectionChangeOnUnsavedDataHandler;
//	private NotificationUpdateController<ENTITY> notificationUpdateController;
	private FormMetaFields metaFields;

	public FormControllerImpl(FormButtonSize buttonSize, Function<FormButton<ENTITY>, ToolbarButtonGroup> toolbarButtonGroupFunction, LifecycleEntityModel<ENTITY> lifecycleEntityModel, EntityPrivileges<ENTITY> entityPrivileges, ApplicationInstanceData applicationInstanceData) {
		this(null, buttonSize, toolbarButtonGroupFunction, lifecycleEntityModel, entityPrivileges, applicationInstanceData);
	}

	public FormControllerImpl(ObservableValue<Boolean> visibilityProvider, FormButtonSize buttonSize, Function<FormButton<ENTITY>, ToolbarButtonGroup> toolbarButtonGroupFunction, LifecycleEntityModel<ENTITY> lifecycleEntityModel, EntityPrivileges<ENTITY> entityPrivileges, ApplicationInstanceData applicationInstanceData) {
		this.visibilityProvider = visibilityProvider;
		this.buttonSize = buttonSize;
		this.toolbarButtonGroupFunction = toolbarButtonGroupFunction;
		this.lifecycleEntityModel = lifecycleEntityModel;
		this.entityPrivileges = entityPrivileges;
		this.applicationInstanceData = applicationInstanceData;
		form = new ResponsiveForm<>(150, 200, 0);
		formLayout = form.addResponsiveFormLayout(500);

		if (visibilityProvider != null) {
			visibilityProvider.onChanged().addListener(visible -> {
				if (visible) {
					ENTITY entity = lifecycleEntityModel.getSelectedEntity().get();
					if (entity == null && lifecycleEntityModel.getNewEntitySupplier() != null) {
						entity = lifecycleEntityModel.getNewEntitySupplier().get();
					}
					handleSelectedEntityChanged(entity);
				}
			});
		}
		lifecycleEntityModel.getSelectedEntity().onChanged().addListener(this::handleSelectedEntityChanged);

		lifecycleEntityModel.getOnSelectedEntityExternallyChanged().addListener(entityUpdateType -> {
			if (visibilityProvider != null && !visibilityProvider.get()) {
				return;
			}
			ENTITY entity = lifecycleEntityModel.getSelectedEntity().get();
			if (entityUpdateType == EntityUpdateType.UPDATE) {
				if (getEntityState() == FormEntityState.STORED_UNCHANGED) {
					lifecycleEntityModel.handleEntitySelection(entity);
				} else if (getEntityState().isModified()) {
					//todo update the unmodified fields?
					//show notification
				}
				Integer userId = (Integer) entity.getEntityValue(TableModel.FIELD_MODIFIED_BY);
//				UiUtils.showNotification(ApplicationIcons.EDIT, getLocalized("apps.theRecordXHasBeenModifiedByY", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData), PropertyProviders.getUserCaptionWithTranslation(User.getById(userId))));
			} else if (entityUpdateType == EntityUpdateType.DELETE) {
				handleFormEntityStateChanged(FormEntityState.NOTHING);
				lifecycleEntityModel.handleEntitySelection(entity);
				Integer userId = (Integer) entity.getEntityValue(TableModel.FIELD_DELETED_BY);
//				UiUtils.showNotification(ApplicationIcons.DELETE, getLocalized("apps.theRecordXHasBeenDeletedByY", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData), PropertyProviders.getUserCaptionWithTranslation(User.getById(userId))));
			}
		});

		setEntitySelectionChangeOnUnsavedDataHandler(createEntitySelectionChangeOnUnsavedDataHandler());
		lifecycleEntityModel.setDeletionDialogue(createDeletionDialogue());
		lifecycleEntityModel.setRestoreDialogue(createRestoreDialogue());

		metaFields = getApplicationInstanceData().getComponentFactory().createFormMetaFields();
		getSelectedFormEntity().onChanged().addListener(entity -> metaFields.updateEntity(entity));
//		lifecycleEntityModel.addUpdateListener(entity -> { //todo remove?
//			if (formEditMode.get() == FormEditMode.EDIT_MODE) {
//				handleFormEditModeChanged(FormEditMode.READ_MODE);
//			}
//		});
	}

	private FormEntityState getEntityState() {
		return formEntityState.get();
	}

	private void handleFormEntityStateChanged(FormEntityState entityState) {
		formEntityState.set(entityState);
		FormEditMode editMode = formEditMode.get();
		if (editMode != FormEditMode.COMBINED_EDIT_AND_READ) {
			if (editMode == FormEditMode.READ_MODE && entityState == FormEntityState.NEW_UNCHANGED) {
				handleFormEditModeChanged(FormEditMode.EDIT_MODE);
			} else if (editMode == FormEditMode.EDIT_MODE && entityState == FormEntityState.STORED_UNCHANGED) {
				handleFormEditModeChanged(FormEditMode.READ_MODE);
			}
		}
	}

	public void handleSelectedEntityChanged(ENTITY entity) {
		if (visibilityProvider != null && !visibilityProvider.get()) {
			return;
		}
		if (blockNextChangeEvent) {
			blockNextChangeEvent = false;
			return;
		}
		if (getEntityState().isModified() && entitySelectionChangeOnUnsavedDataHandler != null) {
			entitySelectionChangeOnUnsavedDataHandler.accept(currentEntity, stay -> {
				if (stay) {
					blockNextChangeEvent = true;
					lifecycleEntityModel.handleEntitySelection(currentEntity);
				} else {
					handleFormEntityStateChanged(FormEntityState.NOTHING);
					handleSelectedEntityChanged(entity);
					blockNextChangeEvent = false;
				}
			});
			return;
		}
		if (entity == null) {
			handleFormEntityStateChanged(FormEntityState.NOTHING);
		} else if (entity.isDeleted()) {
			handleFormEntityStateChanged(FormEntityState.DELETED);
		} else if (entity.isStored()) {
			handleFormEntityStateChanged(FormEntityState.STORED_UNCHANGED);
		} else {
			handleFormEntityStateChanged(FormEntityState.NEW_UNCHANGED);
		}
		currentEntity = entity;
		for (FormButton<ENTITY> button : buttons) {
			updateButton(button, entity, entity);
		}
		formFields.forEach(AbstractField::clearValidatorMessages);
		if (entity == null) {
			synchronizedEditsEntityCopy.set(null);
		} else {
			if (entity.isStored()) {
				synchronizedEditsEntityCopy.set(lifecycleEntityModel.getEntityCopyFunction().apply(entity));
			} else {
				ENTITY entityCopy = lifecycleEntityModel.getNewEntitySupplier() != null ? lifecycleEntityModel.getNewEntitySupplier().get() : null;
				synchronizedEditsEntityCopy.set(entityCopy);
			}
		}
		selectedFormEntity.set(entity);
		markAllFieldsUnchanged();
	}

	public void setEntitySelectionChangeOnUnsavedDataHandler(BiConsumer<ENTITY, Consumer<Boolean>> entitySelectionChangeOnUnsavedDataHandler) {
		this.entitySelectionChangeOnUnsavedDataHandler = entitySelectionChangeOnUnsavedDataHandler;
	}

	@Override
	public void setFormToEntityFunction(Function<ENTITY, Boolean> formToEntityFunction) {
		this.formToEntityFunction = formToEntityFunction;
	}

	@Override
	public ResponsiveForm<ENTITY> getForm() {
		return form;
	}

	@Override
	public ResponsiveFormLayout getFormLayout() {
		return formLayout;
	}

	@Override
	public ObservableValue<ENTITY> getSelectedFormEntity() {
		return selectedFormEntity;
	}

	@Override
	public ObservableValue<ENTITY> getSynchronizedEditsEntityCopy() {
		return synchronizedEditsEntityCopy;
	}

	@Override
	public ObservableValue<FormEntityState> getFormEntityState() {
		return formEntityState;
	}


	@Override
	public ObservableValue<FormEditMode> getFormEditMode() {
		return formEditMode;
	}

	@Override
	public void handleFormEditModeChanged(FormEditMode formEditMode) {
		if (formEditMode != FormEditMode.COMBINED_EDIT_AND_READ) {
			FieldEditingMode fieldEditingMode = formEditMode == FormEditMode.READ_MODE ? FieldEditingMode.READONLY : FieldEditingMode.EDITABLE;
			formFields.forEach(f -> {
				if (initiallyVisibleFields.contains(f)) {
					f.setEditingMode(fieldEditingMode);
				}
			});
		}
		this.formEditMode.set(formEditMode);
	}

	@Override
	public void setForm(ResponsiveForm<ENTITY> form) {
		this.form = form;
		this.formLayout = form.addResponsiveFormLayout(500);
	}

	@Override
	public ResponsiveFormSection addEmptyFormSection() {
		ResponsiveFormSection formSection = formLayout.addSection();
		formSection.setCollapsible(false).setDrawHeaderLine(false).setHideWhenNoVisibleFields(true);
		return formSection;
	}

	@Override
	public ResponsiveFormSection addFormSection(Icon<?, ?> icon, String title) {
		return formLayout.addSection(icon, title);
	}

	@Override
	public ResponsiveFormLayout.LabelAndField addField(String label, AbstractField<?> field) {
		return addField(label, field, null);
	}

	@Override
	public ResponsiveFormLayout.LabelAndField addField(String label, AbstractField<?> field, Consumer<ENTITY> updateEntityConsumer) {
		ResponsiveFormLayout.LabelAndField labelAndField = formLayout.addLabelAndField(null, label, field);
		formFields.add(field);
		if (field.isVisible()) {
			initiallyVisibleFields.add(field);
		}
		if (updateEntityConsumer != null) {
			fieldToEntityHandlerMap.put(field, updateEntityConsumer);
			field.onValueChanged.addListener(() -> {
				ENTITY entity = synchronizedEditsEntityCopy.get();
				if (entity != null) {
					updateEntityConsumer.accept(entity);
				}
			});
		}
		field.onValueChanged.addListener(() -> handleEntityModified(field));
		if (field instanceof TextField textField) {
			textField.onTextInput.addListener(() -> handleEntityModified(field));
		}
		return labelAndField;
	}

	@Override
	public ResponsiveFormLayout.LabelAndField addField(String label, Component component) {
		ResponsiveFormLayout.LabelAndField labelAndField = formLayout.addLabelAndComponent(null, label, component);
		return labelAndField;
	}

	@Override
	public FormPanel addTable(Table<?> table, Icon<?, ?> icon, String caption) {
		FormPanel formPanel = new FormPanel(getApplicationInstanceData());
		formPanel.setTable(table, true, false, false);
		formPanel.addToFormAsSection(formLayout, icon, caption);
		addEmptyFormSection();
		return formPanel;
	}

	@Override
	public <TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field) {
		return addSingleColumnTable(icon, caption, rowHeight, field, null);
	}

	@Override
	public <TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field, Function<TABLE_ENTITY, String> entityStringFunction) {
		FormPanel formPanel = new FormPanel(getApplicationInstanceData());
		ListTable<TABLE_ENTITY> table = new ListTable<>();
		ListTableModel<TABLE_ENTITY> tableModel = (ListTableModel<TABLE_ENTITY>) table.getModel();
		table.setForceFitWidth(true);
		table.setRowHeight(rowHeight);
		table.setHideHeaders(true);
		table.setDisplayAsList(true);
		table.addColumn("col", "", field).setValueExtractor(object -> object);
		formPanel.setTable(table, true, false, false);
		formPanel.addToFormAsSection(formLayout, icon, caption);
		TextField searchField;
		if (entityStringFunction != null) {
			searchField = new TextField();
			searchField.setShowClearButton(true);
			searchField.setEmptyText(getLocalized(Dictionary.SEARCH___));
			searchField.onTextInput.addListener(query -> {
				if (query != null && !query.isBlank()) {
					tableModel.setFilter(entity -> entityStringFunction.apply(entity).toLowerCase().contains(query.toLowerCase()));
				} else {
					tableModel.setFilter(entity -> true);
				}
			});
			formPanel.getPanel().setRightHeaderField(searchField);
		}
		table.getModel().onAllDataChanged().addListener(() -> formPanel.getPanel().setTitle(caption + " (" + table.getRecords().size() + ")"));
		addEmptyFormSection();
		return table;
	}

	@Override
	public void addFields(List<AbstractField<?>> fields) {
		formFields.addAll(fields);
		for (AbstractField<?> field : fields) {
			field.onValueChanged.addListener(() -> handleEntityModified(field));
			if (field.isVisible()) {
				initiallyVisibleFields.add(field);
			}
			if (field instanceof TextField textField) {
				textField.onTextInput.addListener(() -> handleEntityModified(field));
			}
		}
	}

	@Override
	public void addMetaFields() {
		metaFields.addMetaFields(getFormLayout(), false);
	}

	@Override
	public void handleEntityModified(AbstractField<?> field) {
		markFieldChanged(field);
		handleEntityModified();
	}

	@Override
	public void handleEntityModified() {
		ENTITY entity = getSelectedFormEntity().get();
		ENTITY synchronizedEntityCopy = getSynchronizedEditsEntityCopy().get();
		if (entity != null) {
			if (entity.isDeleted()) {
				handleFormEntityStateChanged(FormEntityState.DELETED);
			} else if (entity.isStored()) {
				handleFormEntityStateChanged(FormEntityState.STORED_MODIFIED);
			} else {
				handleFormEntityStateChanged(FormEntityState.NEW_MODIFIED);
			}
		}
		for (FormButton<ENTITY> button : buttons) {
			updateButton(button, entity, synchronizedEntityCopy);
		}
	}

	@Override
	public void handleSaveRequest() {
		handleSaveRequest(null);
	}

	@Override
	public void handleSaveRequest(Function<ENTITY, Boolean> privilegeFunction) {
		ENTITY entity = getSelectedFormEntity().get();
		if (formToEntityFunction == null) {
			return;
		}
		if (Fields.validateAll(formFields)) {
			if (formToEntityFunction.apply(entity)) {
				if (privilegeFunction == null || privilegeFunction.apply(entity)) { //todo check!
					lifecycleEntityModel.saveEntity(entity, () -> {
						handleFormEntityStateChanged(FormEntityState.STORED_UNCHANGED);
						markAllFieldsUnchanged();
						UiUtils.showSaveNotification(true, getApplicationInstanceData());
					});
				} else {
					//todo show missing privileges info
					UiUtils.showSaveNotification(false, getApplicationInstanceData());
				}
			}
		} else {
			UiUtils.showSaveNotification(false, getApplicationInstanceData());
		}
	}

	@Override
	public void handleNewEntityRequest() {
		if (entityPrivileges.isCreateAllowed()) {
			ENTITY newEntity = lifecycleEntityModel.getNewEntitySupplier().get();
			lifecycleEntityModel.handleEntitySelection(newEntity);
		}
	}

	@Override
	public void handleRevertChangesRequest() {
		ENTITY entity = getSelectedFormEntity().get();
		handleFormEntityStateChanged(FormEntityState.NOTHING);
		markAllFieldsUnchanged();
		lifecycleEntityModel.handleEntitySelection(entity);
	}

	@Override
	public void handleDeleteRequest() {
		ENTITY entity = getSelectedFormEntity().get();
		lifecycleEntityModel.deleteEntity(entity);
	}

	@Override
	public void handleRestoreRequest() {
		ENTITY entity = getSelectedFormEntity().get();
		lifecycleEntityModel.restoreEntity(entity);
	}

	@Override
	public void markAllFieldsUnchanged() {
		for (AbstractField<?> field : formFields) {
			field.setCssStyle(".field-border", "border-color", null);
			field.setCssStyle(".field-border-glow", "box-shadow", null);
			if (field instanceof FileField) {
				field.setCssStyle(".list", "box-shadow", null);
				field.setCssStyle(".list", "border", null);
			} else if (field instanceof TemplateField<?>) {
				field.setCssStyle("border-color", null);
				field.setCssStyle("box-shadow", null);
			}
		}
		ENTITY entity = getSelectedFormEntity().get();
		ENTITY synchronizedEntityCopy = getSynchronizedEditsEntityCopy().get();
		for (FormButton<ENTITY> button : buttons) {
			updateButton(button, entity, synchronizedEntityCopy);
		}
	}

	@Override
	public void markFieldChanged(AbstractField<?> field) {
		if (field instanceof FileField<?> fileField && !fileField.isEmpty()) {
			field.setCssStyle(".list", "box-shadow", "0 0 3px 0 #ec9a1a");
			field.setCssStyle(".list", "border", "1px solid #ec9a1a");
		} else if (field instanceof TemplateField<?>) {
			field.setCssStyle("border-color", "#ec9a1a");
			field.setCssStyle("box-shadow", "0 0 4px 0 #ec9a1a");
		} else {
			field.setCssStyle(".field-border", "border-color", "#ec9a1a");
			field.setCssStyle(".field-border-glow", "box-shadow", "0 0 3px 0 #ec9a1a");
		}
	}

	@Override
	public void addButton(FormButton<ENTITY> formButton) {
		createButton(formButton);
	}

	@Override
	public FormButton<ENTITY> addNewEntityButton() {
		return addNewEntityButton(ApplicationIcons.ADD, getLocalized(Dictionary.ADD), getLocalized(Dictionary.ADD_RECORD));
	}

	@Override
	public FormButton<ENTITY> addNewEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isCreateAllowed());
		button.setVisibilityStates(FormEntityState.NOTHING, FormEntityState.STORED_UNCHANGED, FormEntityState.NEW_UNCHANGED);
		button.setEventHandler((entity, synchronizedEntityCopy,entityState) -> {
			handleNewEntityRequest();
		});
		createButton(button);
		return button;
	}

	public FormButton<ENTITY> addEditButton() {
		return addEditButton(ApplicationIcons.EDIT, getLocalized(Dictionary.EDIT), getLocalized(Dictionary.EDIT));
	}

	public FormButton<ENTITY> addEditButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setAllowedFunction((entity, synchronizedEntityCopy,privileges) -> privileges.isCreateAllowed());
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED);
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (entityPrivileges.isCreateAllowed()) {
				handleFormEditModeChanged(FormEditMode.EDIT_MODE);
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addSaveEntityButton() {
		return addSaveEntityButton(ApplicationIcons.FLOPPY_DISKS, getLocalized(Dictionary.SAVE), getLocalized(Dictionary.SAVE));
	}

	@Override
	public FormButton<ENTITY> addSaveEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.NEW_MODIFIED, FormEntityState.STORED_MODIFIED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isSaveAllowed(entity, synchronizedEntityCopy));
		button.setVisibilityFunction((entity, synchronizedEntityCopy,privileges) -> privileges.isSaveOptionAvailable(entity, synchronizedEntityCopy));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			//todo add check to the handle request --> entity must be filled with form data!
//			if (!button.isAllowed(entity, entityPrivileges)) {
//				return;
//			}

			handleSaveRequest(formDataAppliedEntity -> button.isAllowed(formDataAppliedEntity, null, entityPrivileges)); //TODO!!!
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addRevertChangesButton() {
		return addRevertChangesButton(ApplicationIcons.UNDO, applicationInstanceData.getLocalized(Dictionary.REVERT_CHANGES), getLocalized(Dictionary.REVERT_CHANGES));
	}

	@Override
	public FormButton<ENTITY> addRevertChangesButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.NEW_MODIFIED, FormEntityState.STORED_MODIFIED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> true);
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			handleRevertChangesRequest();
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addDeleteEntityButton() {
		return addDeleteEntityButton(ApplicationIcons.GARBAGE_EMPTY, applicationInstanceData.getLocalized(Dictionary.DELETE), getLocalized(Dictionary.DELETE_RECORD));
	}

	@Override
	public FormButton<ENTITY> addDeleteEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isDeleteAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				handleDeleteRequest();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addRestoreEntityButton() {
		return addRestoreEntityButton(CompositeIcon.of(ApplicationIcons.GARBAGE_MAKE_EMPTY, ApplicationIcons.ADD), applicationInstanceData.getLocalized(Dictionary.RESTORE), getLocalized(Dictionary.RESTORE_RECORD));
	}

	@Override
	public FormButton<ENTITY> addRestoreEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.DELETED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isRestoreAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				handleRestoreRequest();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton() {
		return addEntityVersionsViewButton(ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.SHOW_MODIFICATION_HISTORY), getLocalized(Dictionary.SHOW_MODIFICATION_HISTORY));
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED, FormEntityState.DELETED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isModificationHistoryAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				RecordVersionsView<ENTITY> recordVersionsView = new RecordVersionsView<>(entity, applicationInstanceData);
				AbstractUdbEntity<ENTITY> udbEntity = (AbstractUdbEntity<ENTITY>) entity;
				udbEntity.getTableIndex().getFieldIndices().forEach(col -> recordVersionsView.addField(col.getName(), null));
				recordVersionsView.showVersionsWindow();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton(EntityChangeHistoryView<ENTITY> changeHistoryView) {
		FormButton<ENTITY> button = new FormButton<>(ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.SHOW_MODIFICATION_HISTORY), getLocalized(Dictionary.SHOW_MODIFICATION_HISTORY));
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED, FormEntityState.DELETED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isModificationHistoryAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				changeHistoryView.show();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addShowPreviousEntityButton() {
		return addShowPreviousEntityButton(ApplicationIcons.NAVIGATE_LEFT, applicationInstanceData.getLocalized(Dictionary.PREVIOUS), getLocalized(Dictionary.PREVIOUS));
	}

	@Override
	public FormButton<ENTITY> addShowPreviousEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED, FormEntityState.DELETED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isRestoreAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				lifecycleEntityModel.handleSelectPreviousEntity();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addShowNextEntityButton() {
		return addShowNextEntityButton(ApplicationIcons.NAVIGATE_RIGHT, applicationInstanceData.getLocalized(Dictionary.NEXT), getLocalized(Dictionary.NEXT));
	}

	@Override
	public FormButton<ENTITY> addShowNextEntityButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED, FormEntityState.DELETED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> privileges.isRestoreAllowed(entity));
		button.setEventHandler((entity, synchronizedEntityCopy, entityState) -> {
			if (button.isAllowed(entity, synchronizedEntityCopy, entityPrivileges)) {
				lifecycleEntityModel.handleSelectNextEntity();
			}
		});
		createButton(button);
		return button;
	}

	@Override
	public FormButton<ENTITY> addToolsButton() {
		return addToolsButton(ApplicationIcons.GEARWHEEL, getLocalized("apps.tools"), getLocalized("apps.moreOptions")); //todo
	}

	@Override
	public FormButton<ENTITY> addToolsButton(Icon<?, ?> icon, String caption, String description) {
		FormButton<ENTITY> button = new FormButton<>(icon, caption, description);
		button.setVisibilityStates(FormEntityState.STORED_UNCHANGED, FormEntityState.DELETED, FormEntityState.NEW_UNCHANGED);
		button.setAllowedFunction((entity, synchronizedEntityCopy, privileges) -> true);
		createButton(button);
		return button;
	}

	@Override
	public void addStandardFormButtons() {
		addNewEntityButton();
		addEditButton();
		addSaveEntityButton();
		addRevertChangesButton();
		addDeleteEntityButton();
		addRestoreEntityButton();
		addEntityVersionsViewButton();
	}

	@Override
	public void addAllButtons() {
		addNewEntityButton();
		addEditButton();
		addSaveEntityButton();
		addRevertChangesButton();
		addDeleteEntityButton();
		addRestoreEntityButton();
		addShowPreviousEntityButton();
		addShowNextEntityButton();
		addEntityVersionsViewButton();
	}

	private void createButton(FormButton<ENTITY> formButton) {
		buttons.add(formButton);
		ToolbarButton toolbarButton = switch (buttonSize) {
			case EXTRA_LAGE ->
					ToolbarButton.create(formButton.getIcon(), formButton.getCaption(), formButton.getDescription());
			case LARGE ->
					ToolbarButton.createSmall(formButton.getIcon(), formButton.getCaption(), formButton.getDescription());
			case SMALL -> ToolbarButton.createSmall(formButton.getIcon(), formButton.getCaption());
			case TINY -> ToolbarButton.createTiny(formButton.getIcon(), formButton.getCaption());
		};
		if (formButton.isWorkspaceButton()) {
			toolbarButton = ToolbarButton.create(formButton.getIcon(), formButton.getCaption(), formButton.getDescription());
		}
		toolbarButton.setVisible(false);
		ToolbarButtonGroup buttonGroup = null;
		if (formButton.getButtonGroupId() != null) {
			buttonGroup = toolbarButtonGroupMap.get(formButton.getButtonGroupId());
		}
		if (buttonGroup == null) {
			buttonGroup = toolbarButtonGroupFunction.apply(formButton);
			if (formButton.getButtonGroupId() != null) {
				toolbarButtonGroupMap.put(formButton.getButtonGroupId(), buttonGroup);
			}
		}
		buttonGroup.addButton(toolbarButton);
		buttonMap.put(formButton, toolbarButton);
		if (formButton.getMenuButtons().isEmpty()) {
			toolbarButton.onClick.addListener(event -> {
				ENTITY entity = getSelectedFormEntity().get();
				ENTITY synchronizedEntity = getSynchronizedEditsEntityCopy().get();
				if (isButtonVisible(formButton, entity, synchronizedEntity, getEntityState())) {
					formButton.getEventHandler().handleButtonClick(entity, synchronizedEntity, getEntityState());
				}
			});
		} else {
			SimpleItemView<FormButton<ENTITY>> itemView = new SimpleItemView<>();
			itemViewMap.put(formButton, itemView);
		}
	}

	private boolean isButtonVisible(FormButton<ENTITY> formButton, ENTITY entity, ENTITY synchronizedEntityCopy, FormEntityState state) {
		if (!formButton.getVisibleOnStates().contains(state)) {
			return false;
		} else {
			return formButton.isVisibilityAllowed(entity, synchronizedEntityCopy, entityPrivileges);
		}
	}

	private void updateButton(FormButton<ENTITY> formButton, ENTITY entity, ENTITY synchronizedEntityCopy) {
		boolean visible = isButtonVisible(formButton, entity, synchronizedEntityCopy, getEntityState());
		if (formButton.isVisible() != visible) {
			ToolbarButton toolbarButton = buttonMap.get(formButton);
			toolbarButton.setVisible(visible);
			formButton.setVisible(visible);
		}
		if (!formButton.getMenuButtons().isEmpty()) {
			boolean updateRequired = false;
			for (FormButton<ENTITY> menuButton : formButton.getMenuButtons()) {
				boolean menuButtonVisible = isButtonVisible(menuButton, entity, synchronizedEntityCopy, getEntityState());
				if (menuButton.isVisible() != menuButtonVisible) {
					updateRequired = true;
					break;
				}
			}
			if (updateRequired) {
				SimpleItemView<FormButton<ENTITY>> itemView = itemViewMap.get(formButton);
				itemView.removeAllGroups();
				SimpleItemGroup<FormButton<ENTITY>> itemGroup = itemView.addSingleColumnGroup(formButton.getIcon(), formButton.getCaption());
				for (FormButton<ENTITY> menuButton : formButton.getMenuButtons()) {
					boolean menuButtonVisible = isButtonVisible(menuButton, entity, synchronizedEntityCopy, getEntityState());
					menuButton.setVisible(menuButtonVisible);
					if (menuButtonVisible) {
						itemGroup.addItem(menuButton.getIcon(), menuButton.getCaption(), menuButton.getDescription()).onClick.addListener(event -> {
							FormEntityState entityState2 = getEntityState();
							ENTITY entity2 = getSelectedFormEntity().get();
							ENTITY synchronizedEntityCopy2 = getSynchronizedEditsEntityCopy().get();
							if (isButtonVisible(menuButton, entity2, synchronizedEntityCopy2, entityState2)) {
								menuButton.getEventHandler().handleButtonClick(entity2, synchronizedEntityCopy2, entityState2);
							}
						});
					}
				}
			}

		}
	}

	@Override
	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	private BiConsumer<ENTITY, Consumer<Boolean>> createEntitySelectionChangeOnUnsavedDataHandler() {
		return (entity, stayOnRecordConsumer) -> {
			FormDialogue formDialogue = FormDialogue.create(ApplicationIcons.DOOR_OPEN, getLocalized("apps.thereIsUnsavedData"), getLocalized("apps.thereIsUnsavedData.desc"));
			formDialogue.setSize(500, 200);
			formDialogue.setCloseable(true);
			formDialogue.setCloseOnEscape(true);
			formDialogue.setAutoCloseOnOk(true);
			formDialogue.addOkButton(ApplicationIcons.DOOR_OPEN, getLocalized("apps.changeWithoutSaving"));
			formDialogue.addCancelButton(ApplicationIcons.EDIT, getLocalized("apps.back"));
			formDialogue.onOk.addListener(() -> stayOnRecordConsumer.accept(false));
			formDialogue.onCancel.addListener(() -> stayOnRecordConsumer.accept(true));
			formDialogue.show();

		};
	}

	private BiConsumer<ENTITY, Runnable> createDeletionDialogue() {
		return (entity, runnable) -> {
			FormDialogue formDialogue = FormDialogue.create(ApplicationIcons.ERROR, getLocalized("apps.doYouReallyWantToDelete", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData)), getLocalized("apps.doYouReallyWantToDelete.desc", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData)));
			formDialogue.setSize(500, 200);
			formDialogue.setCloseable(true);
			formDialogue.setCloseOnEscape(true);
			formDialogue.setAutoCloseOnOk(true);
			formDialogue.addOkButton(ApplicationIcons.ERROR, getLocalized(Dictionary.DELETE));
			formDialogue.addCancelButton(ApplicationIcons.WINDOW_CLOSE, getLocalized(Dictionary.CANCEL));
			formDialogue.onOk.addListener(runnable);
			formDialogue.onCancel.addListener(() -> formDialogue.close());
			formDialogue.show();
		};
	}

	private BiConsumer<ENTITY, Runnable> createRestoreDialogue() {
		return (entity, runnable) -> {
			FormDialogue formDialogue = FormDialogue.create(ApplicationIcons.GARBAGE_MAKE_EMPTY, getLocalized("apps.doYouReallyWantToRestore", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData)), getLocalized("apps.doYouReallyWantToRestore.desc", lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData)));
			formDialogue.setSize(500, 200);
			formDialogue.setCloseable(true);
			formDialogue.setCloseOnEscape(true);
			formDialogue.setAutoCloseOnOk(true);
			formDialogue.addOkButton(ApplicationIcons.GARBAGE_MAKE_EMPTY, getLocalized(Dictionary.RESTORE));
			formDialogue.addCancelButton(ApplicationIcons.WINDOW_CLOSE, getLocalized(Dictionary.CANCEL));
			formDialogue.onOk.addListener(runnable);
			formDialogue.onCancel.addListener(() -> formDialogue.close());
			formDialogue.show();
		};
	}


	@Override
	public NotificationUpdateController<ENTITY> getNotificationUpdateController(Function<ENTITY, OrganizationUnitView> unitByEntityFunction) {
		return lifecycleEntityModel.getNotificationUpdateController(unitByEntityFunction);
//		if (notificationUpdateController == null) {
//			notificationUpdateController = NotificationUpdateController.createNotificationUpdateController(lifecycleEntityModel, unitByEntityFunction, applicationInstanceData);
//		}
//		return notificationUpdateController;
	}

	@Override
	public Event<EntityUpdateType> getOnSelectedEntityExternallyChanged() {
		return lifecycleEntityModel.getOnSelectedEntityExternallyChanged();
	}

	@Override
	public Supplier<ENTITY> getNewEntitySupplier() {
		return lifecycleEntityModel.getNewEntitySupplier();
	}

	public Function<ENTITY, ENTITY> getEntityCopyFunction() {
		return lifecycleEntityModel.getEntityCopyFunction();
	}

	@Override
	public ObservableValue<ENTITY> getSelectedEntity() {
		return selectedFormEntity;
	}

	@Override
	public String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		return lifecycleEntityModel.getEntityTitle(entity, applicationInstanceData);
	}

	@Override
	public void setDeletionDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		lifecycleEntityModel.setDeletionDialogue(dialogueHandler);
	}

	@Override
	public void setRestoreDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		lifecycleEntityModel.setRestoreDialogue(dialogueHandler);
	}

	@Override
	public void setSaveDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		lifecycleEntityModel.setSaveDialogue(dialogueHandler);
	}

	@Override
	public void addDeletionValidator(Function<ENTITY, ValidationMessage> validator) {
		lifecycleEntityModel.addDeletionValidator(validator);
	}

	@Override
	public void addRestoreValidator(Function<ENTITY, ValidationMessage> validator) {
		lifecycleEntityModel.addRestoreValidator(validator);
	}

	@Override
	public void addSaveValidator(Function<ENTITY, ValidationMessage> validator) {
		lifecycleEntityModel.addSaveValidator(validator);
	}

	@Override
	public void addCreationListener(Consumer<ENTITY> listener) {
		lifecycleEntityModel.addCreationListener(listener);
	}

	@Override
	public void addUpdateListener(Consumer<ENTITY> listener) {
		lifecycleEntityModel.addUpdateListener(listener);
	}

	@Override
	public void addDeletionListener(Consumer<ENTITY> listener) {
		lifecycleEntityModel.addDeletionListener(listener);
	}

	@Override
	public void addRestoreListener(Consumer<ENTITY> listener) {
		lifecycleEntityModel.addRestoreListener(listener);
	}

	@Override
	public void addBeforeUpdateAndAfterUpdateHandler(BiFunction<ENTITY, ENTITY, Runnable> beforeUpdateAndAfterHandler) {
		lifecycleEntityModel.addBeforeUpdateAndAfterUpdateHandler(beforeUpdateAndAfterHandler);
	}

	@Override
	public void handleEntitySelection(ENTITY record) {
		lifecycleEntityModel.handleEntitySelection(record);
	}

	@Override
	public void handleSelectPreviousEntity() {
		lifecycleEntityModel.handleSelectPreviousEntity();
	}

	@Override
	public void handleSelectNextEntity() {
		lifecycleEntityModel.handleSelectNextEntity();
	}

	@Override
	public void handleSelectFirstEntity() {
		lifecycleEntityModel.handleSelectFirstEntity();
	}

	@Override
	public void saveEntity(ENTITY entity) {
		lifecycleEntityModel.saveEntity(entity);
	}

	@Override
	public void saveEntity(ENTITY entity, Runnable onSuccessRunnable) {
		lifecycleEntityModel.saveEntity(entity, onSuccessRunnable);
	}

	@Override
	public void deleteEntity(ENTITY entity) {
		lifecycleEntityModel.deleteEntity(entity);
	}

	@Override
	public void deleteEntity(ENTITY entity, Runnable onSuccessRunnable) {
		lifecycleEntityModel.deleteEntity(entity, onSuccessRunnable);
	}

	@Override
	public void restoreEntity(ENTITY entity) {
		lifecycleEntityModel.restoreEntity(entity);
	}

	@Override
	public void restoreEntity(ENTITY entity, Runnable onSuccessRunnable) {
		lifecycleEntityModel.restoreEntity(entity, onSuccessRunnable);
	}

	@Override
	public boolean isCreateAllowed() {
		return entityPrivileges.isCreateAllowed();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		return entityPrivileges.isSaveOptionAvailable(entity,synchronizedEntityCopy);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		return entityPrivileges.isSaveAllowed(entity, synchronizedEntityCopy);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		return entityPrivileges.isDeleteAllowed(entity);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		return entityPrivileges.isRestoreAllowed(entity);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		return entityPrivileges.isModificationHistoryAllowed(entity);
	}
}
