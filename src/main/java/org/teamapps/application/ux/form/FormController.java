package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.StandardPrivilegeGroup;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.databinding.MutableValue;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.dialogue.Dialogue;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.Fields;
import org.teamapps.ux.component.form.AbstractForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FormController<ENTITY extends Entity<?>> extends FormValidator {


	private final AbstractForm<?, ENTITY> form;
	private final Set<AbstractField<?>> otherFields = new HashSet<>();
	private final ObservableValue<ENTITY> selectedEntity;
	private final ApplicationInstanceData applicationInstanceData;

	private FormControllerEventHandler<ENTITY> eventHandler;

	private StandardPrivilegeGroup standardPrivilegeGroup;

	private OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private AbstractField<OrganizationUnitView> organizationUnitViewField;

	private boolean updateFieldEditMode;
	private Set<AbstractField<?>> nonEditableFields = new HashSet<>();

	private ToolbarButton newButton;
	private ToolbarButton saveButton;
	private ToolbarButton revertButton;
	private ToolbarButton deleteButton;
	private ToolbarButton restoreButton;


	public FormController(ApplicationInstanceData applicationInstanceData, AbstractForm<?, ENTITY> form, ObservableValue<ENTITY> selectedEntity, StandardPrivilegeGroup standardPrivilegeGroup) {
		super(applicationInstanceData);
		this.form = form;
		this.selectedEntity = selectedEntity;
		this.applicationInstanceData = applicationInstanceData;
		this.standardPrivilegeGroup = standardPrivilegeGroup;
		createToolbarButtons(applicationInstanceData);
		form.onFieldValueChanged.addListener(event -> handleFieldUpdateByClient(event.getField()));
		selectedEntity.onChanged().addListener(this::handleEntitySelection);
	}

	public FormController(ApplicationInstanceData applicationInstanceData, AbstractForm<?, ENTITY> form, ObservableValue<ENTITY> selectedEntity, OrganizationalPrivilegeGroup organizationalPrivilegeGroup) {
		super(applicationInstanceData);
		this.form = form;
		this.selectedEntity = selectedEntity;
		this.applicationInstanceData = applicationInstanceData;
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		createToolbarButtons(applicationInstanceData);
		form.onFieldValueChanged.addListener(event -> handleFieldUpdateByClient(event.getField()));
		selectedEntity.onChanged().addListener(this::handleEntitySelection);

		List<OrganizationUnitView> allowedUnitsForCreation = applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE);
		List<OrganizationUnitView> allowedUnitsForModification = applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.UPDATE);
		if (allowedUnitsForCreation.size() > 1 || allowedUnitsForModification.size() > 1) {
			organizationUnitViewField = applicationInstanceData.getComponentFactory().createOrganizationUnitComboBox(() -> {
				if (selectedEntity.get().isStored()) {
					return allowedUnitsForModification;
				} else {
					return allowedUnitsForCreation;
				}
			});
		} else {
			organizationUnitViewField = applicationInstanceData.getComponentFactory().createOrganizationUnitTemplateField();
		}
		organizationUnitViewField.addValidator(organizationUnitView -> {
			ENTITY entity = selectedEntity.get();
			Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
			if (applicationInstanceData.isAllowed(organizationalPrivilegeGroup, privilege, organizationUnitViewField.getValue())) {
				return Collections.emptyList();
			} else {
				return Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR, applicationInstanceData.getLocalized(Dictionary.ORGANIZATION))); //todo change error message
			}
		});
		addFieldWithValidator(organizationUnitViewField);
	}

	public void setEventHandler(FormControllerEventHandler<ENTITY> eventHandler) {
		this.eventHandler = eventHandler;
	}

	public void setEventHandler(Runnable createNewEntityRunnable, Predicate<ENTITY> saveEntityPredicate) {
		this.eventHandler = new FormControllerEventHandler<>() {
			@Override
			public boolean handleNewEntityRequest() {
				createNewEntityRunnable.run();
				return true;
			}

			@Override
			public boolean handleSaveRequest(ENTITY entity) {
				return saveEntityPredicate.test(entity);
			}

			@Override
			public boolean handleRevertChangesRequest(ENTITY entity) {
				selectedEntity.onChanged().fire(entity);
				return true;
			}

			@Override
			public boolean handleDeleteRequest(ENTITY entity) {
				entity.delete();
				return true;
			}

			@Override
			public boolean handleRestoreDeletedRecordRequest(ENTITY entity) {
				entity.restoreDeleted();
				return true;
			}
		};
	}

	public void setUpdateFieldEditMode(boolean updateFieldEditMode) {
		this.updateFieldEditMode = updateFieldEditMode;
		if (updateFieldEditMode) {
			nonEditableFields.clear();
			Stream.concat(form.getFields().stream(), otherFields.stream())
					.filter(f -> f.getEditingMode() != FieldEditingMode.EDITABLE)
					.forEach(f -> nonEditableFields.add(f));
		}
	}

	public void addFormFields(List<AbstractField<?>> fields) {
		fields.stream().filter(f -> !otherFields.contains(f)).forEach(f -> {
			otherFields.add(f);
			handleFieldUpdateByClient(f);
		});
	}

	private void createToolbarButtons(ApplicationInstanceData applicationInstanceData) {
		newButton = FormButtonUtils.createNewButton(applicationInstanceData);
		saveButton = FormButtonUtils.createSaveButton(applicationInstanceData);
		revertButton = FormButtonUtils.createRevertButton(applicationInstanceData);
		deleteButton = FormButtonUtils.createDeleteButton(applicationInstanceData);
		restoreButton = FormButtonUtils.createRestoreButton(applicationInstanceData);

		newButton.setVisible(isEntityCreationAllowed());
		saveButton.setVisible(false);
		revertButton.setVisible(false);
		deleteButton.setVisible(false);
		restoreButton.setVisible(false);

		newButton.onClick.addListener(() -> {
			if (isEntityCreationAllowed()) {
				if (eventHandler != null && eventHandler.handleNewEntityRequest()) {
					newButton.setVisible(false);
				}
			}
		});

		saveButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (validate() && eventHandler != null && eventHandler.handleSaveRequest(entity)) {
				entity.save();
				saveButton.setVisible(false);
				restoreButton.setVisible(false);
				newButton.setVisible(isEntityCreationAllowed());
				UiUtils.showSaveNotification(true, applicationInstanceData);
			} else {
				UiUtils.showSaveNotification(false, applicationInstanceData);
			}
		});

		revertButton.onClick.addListener(() -> {
			if (eventHandler != null && eventHandler.handleRevertChangesRequest(selectedEntity.get())) {
				saveButton.setVisible(false);
				revertButton.setVisible(false);
				newButton.setVisible(isEntityCreationAllowed());
				deleteButton.setVisible(isEntityDeletable(selectedEntity.get()));
				markAllFieldsUnchanged();
			}
		});

		deleteButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (isEntityDeletable(entity)) {
				Dialogue.showOkCancel(ApplicationIcons.DELETE, applicationInstanceData.getLocalized(Dictionary.DELETE_RECORD), applicationInstanceData.getLocalized(Dictionary.SENTENCE_DO_YOU_REALLY_WANT_TO_DELETE_THE_RE__)).addListener(result -> {
					if (result) {
						if (eventHandler != null && eventHandler.handleDeleteRequest(entity)) {
							deleteButton.setVisible(false);
						}
					}
				});
			}
		});

		restoreButton.onClick.addListener(() -> {
			ENTITY entity = selectedEntity.get();
			if (isEntityRestorable(entity)) {
				if (eventHandler != null && eventHandler.handleRestoreDeletedRecordRequest(entity)) {
					restoreButton.setVisible(false);
					deleteButton.setVisible(isEntityDeletable(selectedEntity.get()));
				}
			}
		});

		selectedEntity.onChanged().addListener(entity -> {
			newButton.setVisible(isEntityCreationAllowed());
			deleteButton.setVisible(isEntityDeletable(entity));
			restoreButton.setVisible(isEntityRestorable(entity));
			markAllFieldsUnchanged();
		});
	}

	private void handleEntitySelection(ENTITY entity) {
		if (this.updateFieldEditMode) {
			boolean editable = !entity.isDeleted() && isEntityEditable(entity);
			Stream.concat(form.getFields().stream(), otherFields.stream())
					.filter(f -> !nonEditableFields.contains(f))
					.forEach(f -> f.setEditingMode(editable ? FieldEditingMode.EDITABLE : FieldEditingMode.READONLY));
		}
	}

	public void handleFieldUpdateByClient(AbstractField<?> field) {
		markFieldChanged(field);
		if (!saveButton.isVisible()) {
			ENTITY entity = selectedEntity.get();
			if (isEntityEditable(entity)) {
				saveButton.setVisible(true);
				revertButton.setVisible(true);
				newButton.setVisible(false);
				deleteButton.setVisible(false);
			}
		}
	}

	private boolean isEntityEditable(ENTITY entity) {
		if (entity == null) {
			return false;
		}
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		if (standardPrivilegeGroup != null) {
			return applicationInstanceData.isAllowed(standardPrivilegeGroup, privilege);
		} else {
			return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, privilege, organizationUnitViewField.getValue());
		}
	}

	private boolean isEntityDeletable(ENTITY entity) {
		if (entity == null || !entity.isStored() || entity.isDeleted()) {
			return false;
		}
		Privilege privilege = Privilege.DELETE;
		if (standardPrivilegeGroup != null) {
			return applicationInstanceData.isAllowed(standardPrivilegeGroup, privilege);
		} else {
			return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, privilege, organizationUnitViewField.getValue());
		}
	}

	private boolean isEntityRestorable(ENTITY entity) {
		if (entity == null || !entity.isDeleted() || !entity.isRestorable()) {
			return false;
		}
		Privilege privilege = Privilege.RESTORE;
		if (standardPrivilegeGroup != null) {
			return applicationInstanceData.isAllowed(standardPrivilegeGroup, privilege);
		} else {
			return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, privilege, organizationUnitViewField.getValue());
		}
	}

	private boolean isEntityCreationAllowed() {
		return standardPrivilegeGroup != null ? applicationInstanceData.isAllowed(standardPrivilegeGroup, Privilege.CREATE) : !applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
	}

	public AbstractField<OrganizationUnitView> getOrganizationUnitViewField() {
		return organizationUnitViewField;
	}

	public List<ToolbarButtonGroup> getToolbarButtonGroups() {
		List<ToolbarButtonGroup> buttonGroups = new ArrayList<>();
		ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
		buttonGroup.addButton(newButton);
		buttonGroup.addButton(saveButton);
		buttonGroups.add(buttonGroup);
		buttonGroup = new ToolbarButtonGroup();
		buttonGroup.addButton(revertButton);
		buttonGroups.add(buttonGroup);
		buttonGroup = new ToolbarButtonGroup();
		buttonGroup.addButton(deleteButton);
		buttonGroup.addButton(restoreButton);
		buttonGroups.add(buttonGroup);
		return buttonGroups;
	}

	public void addMetaDataSection(ResponsiveFormLayout formLayout) {
		FormMetaFields formMetaFields = applicationInstanceData.getComponentFactory().createFormMetaFields();
		formMetaFields.addMetaFields(formLayout, false);
		selectedEntity.onChanged().addListener(formMetaFields::updateEntity);
	}

	protected void markFieldChanged(AbstractField<?> field) {
		field.setCssStyle(".field-border", "border-color", "#ec9a1a");
		field.setCssStyle(".field-border-glow", "box-shadow", "0 0 3px 0 #ec9a1a");
	}

	protected void markAllFieldsUnchanged() {
		form.getFields().forEach(field-> {
			field.setCssStyle(".field-border", "border-color", null);
			field.setCssStyle(".field-border-glow", "box-shadow", null);
		});
	}

	@Override
	public boolean validate() {
		boolean validationResult = super.validate();
		if (validationResult) {
			validationResult = Fields.validateAll(new ArrayList<>(otherFields));
			if (validationResult) {
				ENTITY entity = selectedEntity.get();
				return isEntityEditable(entity);
			}
		}
		return false;
	}

	public ToolbarButton getNewButton() {
		return newButton;
	}

	public ToolbarButton getSaveButton() {
		return saveButton;
	}

	public ToolbarButton getRevertButton() {
		return revertButton;
	}

	public ToolbarButton getDeleteButton() {
		return deleteButton;
	}

	public ToolbarButton getRestoreButton() {
		return restoreButton;
	}
}
