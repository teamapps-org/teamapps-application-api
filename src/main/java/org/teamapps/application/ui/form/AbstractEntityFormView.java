package org.teamapps.application.ui.form;

import org.teamapps.application.api.application.AbstractLazyRenderingApplicationView;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.entity.EntityUpdateType;
import org.teamapps.application.ui.changehistory.EntityChangeHistoryView;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.application.ui.model.ValidationMessage;
import org.teamapps.application.ui.notification.NotificationUpdateController;
import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.application.ux.form.FormPanel;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.table.ListTable;
import org.teamapps.ux.component.table.Table;

import java.util.List;
import java.util.function.*;

public abstract class AbstractEntityFormView<ENTITY extends Entity<ENTITY>> extends AbstractLazyRenderingApplicationView implements FormController<ENTITY> {

	private final FormControllerImpl<ENTITY> formController;
	private boolean initialised;

	public AbstractEntityFormView(FormButtonSize buttonSize, LifecycleEntityModel<ENTITY> lifecycleEntityModel, EntityPrivileges<ENTITY> entityPrivileges, ApplicationInstanceData applicationInstanceData) {
		super(applicationInstanceData);
		TwoWayBindableValue<Boolean> visible = TwoWayBindableValue.create();
		onViewRedrawRequired.addListener(() -> visible.set(isVisible()));
		visible.set(isVisible());
		formController = new FormControllerImpl<>(visible, buttonSize, button -> createToolbarButtonGroup(!button.isWorkspaceButton()), lifecycleEntityModel, entityPrivileges, applicationInstanceData);
		onViewRedrawRequired.addListener(() -> {
			if (!initialised) {
				initialised = true;
//				addStandardFormButtons();
			}
		});
	}

	public FormControllerImpl<ENTITY> getFormController() {
		return formController;
	}

	@Override
	public void createUi() {
		Function<ENTITY, Boolean> formToEntityHandler = createForm();
		formController.setFormToEntityFunction(formToEntityHandler);
	}

	public abstract Function<ENTITY, Boolean> createForm();


	@Override
	public void setFormToEntityFunction(Function<ENTITY, Boolean> formToEntityHandler) {
		formController.setFormToEntityFunction(formToEntityHandler);
	}

	@Override
	public ObservableValue<ENTITY> getSelectedFormEntity() {
		return formController.getSelectedFormEntity();
	}

	@Override
	public ObservableValue<FormEntityState> getFormEntityState() {
		return formController.getFormEntityState();
	}

	@Override
	public ObservableValue<FormEditMode> getFormEditMode() {
		return formController.getFormEditMode();
	}

	@Override
	public void handleFormEditModeChanged(FormEditMode formEditMode) {
		formController.handleFormEditModeChanged(formEditMode);
	}

	@Override
	public Component getViewComponent() {
		return formController.getForm();
	}

	@Override
	public ResponsiveForm<ENTITY> getForm() {
		return formController.getForm();
	}

	@Override
	public ResponsiveFormLayout getFormLayout() {
		return formController.getFormLayout();
	}

	@Override
	public void setForm(ResponsiveForm<ENTITY> form) {
		formController.setForm(form);
	}

	@Override
	public ResponsiveFormSection addEmptyFormSection() {
		return formController.addEmptyFormSection();
	}

	@Override
	public ResponsiveFormSection addFormSection(Icon<?, ?> icon, String title) {
		return formController.addFormSection(icon, title);
	}

	@Override
	public ResponsiveFormLayout.LabelAndField addField(String label, AbstractField<?> field) {
		return formController.addField(label, field);
	}

	@Override
	public ResponsiveFormLayout.LabelAndField addField(String label, Component component) {
		return formController.addField(label, component);
	}

	@Override
	public FormPanel addTable(Table<?> table, Icon<?, ?> icon, String caption) {
		return formController.addTable(table, icon, caption);
	}

	@Override
	public <TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field) {
		return formController.addSingleColumnTable(icon, caption, rowHeight, field);
	}

	@Override
	public <TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field, Function<TABLE_ENTITY, String> entityStringFunction) {
		return formController.addSingleColumnTable(icon, caption, rowHeight, field, entityStringFunction);
	}

	@Override
	public void addFields(List<AbstractField<?>> fields) {
		formController.addFields(fields);
	}

	@Override
	public void addMetaFields() {
		formController.addMetaFields();
	}

	@Override
	public void handleEntityModified() {
		formController.handleEntityModified();
	}

	@Override
	public void handleEntityModified(AbstractField<?> field) {
		formController.handleEntityModified(field);
	}

	@Override
	public void handleNewEntityRequest() {
		formController.handleNewEntityRequest();
	}

	@Override
	public void handleSaveRequest() {
		formController.handleSaveRequest();
	}

	@Override
	public void handleSaveRequest(Function<ENTITY, Boolean> privilegeFunction) {
		formController.handleSaveRequest(privilegeFunction);
	}

	@Override
	public void handleRevertChangesRequest() {
		formController.handleRevertChangesRequest();
	}

	@Override
	public void handleDeleteRequest() {
		formController.handleDeleteRequest();
	}

	@Override
	public void handleRestoreRequest() {
		formController.handleRestoreRequest();
	}

	@Override
	public void markAllFieldsUnchanged() {
		formController.markAllFieldsUnchanged();
	}

	@Override
	public void markFieldChanged(AbstractField<?> field) {
		formController.markFieldChanged(field);
	}

	@Override
	public void addButton(FormButton<ENTITY> formButton) {
		formController.addButton(formButton);
	}

	@Override
	public FormButton<ENTITY> addNewEntityButton() {
		return formController.addNewEntityButton();
	}

	@Override
	public FormButton<ENTITY> addNewEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addNewEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addEditButton() {
		return formController.addEditButton();
	}

	@Override
	public FormButton<ENTITY> addEditButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addEditButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addSaveEntityButton() {
		return formController.addSaveEntityButton();
	}

	@Override
	public FormButton<ENTITY> addSaveEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addSaveEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addRevertChangesButton() {
		return formController.addRevertChangesButton();
	}

	@Override
	public FormButton<ENTITY> addRevertChangesButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addRevertChangesButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addDeleteEntityButton() {
		return formController.addDeleteEntityButton();
	}

	@Override
	public FormButton<ENTITY> addDeleteEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addDeleteEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addRestoreEntityButton() {
		return formController.addRestoreEntityButton();
	}

	@Override
	public FormButton<ENTITY> addRestoreEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addRestoreEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton() {
		return formController.addEntityVersionsViewButton();
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addEntityVersionsViewButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addEntityVersionsViewButton(EntityChangeHistoryView<ENTITY> changeHistoryView) {
		return formController.addEntityVersionsViewButton(changeHistoryView);
	}

	@Override
	public FormButton<ENTITY> addShowPreviousEntityButton() {
		return formController.addShowPreviousEntityButton();
	}

	@Override
	public FormButton<ENTITY> addShowPreviousEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addShowPreviousEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addShowNextEntityButton() {
		return formController.addShowNextEntityButton();
	}

	@Override
	public FormButton<ENTITY> addShowNextEntityButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addShowNextEntityButton(icon, caption, description);
	}

	@Override
	public FormButton<ENTITY> addToolsButton() {
		return formController.addToolsButton();
	}

	@Override
	public FormButton<ENTITY> addToolsButton(Icon<?, ?> icon, String caption, String description) {
		return formController.addToolsButton(icon, caption, description);
	}

	@Override
	public void addStandardFormButtons() {
		formController.addStandardFormButtons();
	}

	@Override
	public void addAllButtons() {
		formController.addAllButtons();
	}

	@Override
	public NotificationUpdateController<ENTITY> getNotificationUpdateController(Function<ENTITY, OrganizationUnitView> unitByEntityFunction) {
		return formController.getNotificationUpdateController(unitByEntityFunction);
	}

	@Override
	public Event<EntityUpdateType> getOnSelectedEntityExternallyChanged() {
		return formController.getOnSelectedEntityExternallyChanged();
	}

	@Override
	public Supplier<ENTITY> getNewEntitySupplier() {
		return formController.getNewEntitySupplier();
	}

	@Override
	public ObservableValue<ENTITY> getSelectedEntity() {
		return formController.getSelectedEntity();
	}

	@Override
	public String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		return formController.getEntityTitle(entity, applicationInstanceData);
	}

	@Override
	public void setDeletionDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		formController.setDeletionDialogue(dialogueHandler);
	}

	@Override
	public void setRestoreDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		formController.setRestoreDialogue(dialogueHandler);
	}

	@Override
	public void setSaveDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler) {
		formController.setSaveDialogue(dialogueHandler);
	}

	@Override
	public void addDeletionValidator(Function<ENTITY, ValidationMessage> validator) {
		formController.addDeletionValidator(validator);
	}

	@Override
	public void addRestoreValidator(Function<ENTITY, ValidationMessage> validator) {
		formController.addRestoreValidator(validator);
	}

	@Override
	public void addSaveValidator(Function<ENTITY, ValidationMessage> validator) {
		formController.addSaveValidator(validator);
	}

	@Override
	public void addCreationListener(Consumer<ENTITY> listener) {
		formController.addCreationListener(listener);
	}

	@Override
	public void addUpdateListener(Consumer<ENTITY> listener) {
		formController.addUpdateListener(listener);
	}

	@Override
	public void addDeletionListener(Consumer<ENTITY> listener) {
		formController.addDeletionListener(listener);
	}

	@Override
	public void addRestoreListener(Consumer<ENTITY> listener) {
		formController.addRestoreListener(listener);
	}

	@Override
	public void addBeforeUpdateAndAfterUpdateHandler(BiFunction<ENTITY, ENTITY, Runnable> beforeUpdateAndAfterHandler) {
		formController.addBeforeUpdateAndAfterUpdateHandler(beforeUpdateAndAfterHandler);
	}

	@Override
	public void handleEntitySelection(ENTITY record) {
		formController.handleEntitySelection(record);
	}

	@Override
	public void handleSelectPreviousEntity() {
		formController.handleSelectPreviousEntity();
	}

	@Override
	public void handleSelectNextEntity() {
		formController.handleSelectNextEntity();
	}

	@Override
	public void handleSelectFirstEntity() {
		formController.handleSelectFirstEntity();
	}

	@Override
	public void saveEntity(ENTITY entity) {
		formController.saveEntity(entity);
	}

	@Override
	public void saveEntity(ENTITY entity, Runnable onSuccessRunnable) {
		formController.saveEntity(entity, onSuccessRunnable);
	}

	@Override
	public void deleteEntity(ENTITY entity) {
		formController.deleteEntity(entity);
	}

	@Override
	public void deleteEntity(ENTITY entity, Runnable onSuccessRunnable) {
		formController.deleteEntity(entity, onSuccessRunnable);
	}

	@Override
	public void restoreEntity(ENTITY entity) {
		formController.restoreEntity(entity);
	}

	@Override
	public void restoreEntity(ENTITY entity, Runnable onSuccessRunnable) {
		formController.restoreEntity(entity, onSuccessRunnable);
	}

	@Override
	public boolean isCreateAllowed() {
		return formController.isCreateAllowed();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		return formController.isSaveOptionAvailable(entity);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		return formController.isSaveAllowed(entity);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		return formController.isDeleteAllowed(entity);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		return formController.isRestoreAllowed(entity);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		return formController.isModificationHistoryAllowed(entity);
	}
}
