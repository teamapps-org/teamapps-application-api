package org.teamapps.application.ui.form;

import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ui.changehistory.EntityChangeHistoryView;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.application.ui.privilege.EntityPrivileges;
import org.teamapps.application.ux.form.FormPanel;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.icons.Icon;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.form.ResponsiveForm;
import org.teamapps.ux.component.form.ResponsiveFormLayout;
import org.teamapps.ux.component.form.ResponsiveFormSection;
import org.teamapps.ux.component.table.ListTable;
import org.teamapps.ux.component.table.Table;

import java.util.List;
import java.util.function.Function;

public interface FormController<ENTITY> extends LifecycleEntityModel<ENTITY>, EntityPrivileges<ENTITY> {

	void setForm(ResponsiveForm<ENTITY> form);

	void setFormToEntityFunction(Function<ENTITY, Boolean> formToEntityHandler);

	ResponsiveForm<ENTITY> getForm();

	ResponsiveFormLayout getFormLayout();

	ObservableValue<ENTITY> getSelectedFormEntity();

	ObservableValue<FormEntityState> getFormEntityState();

	ObservableValue<FormEditMode> getFormEditMode();

	void handleFormEditModeChanged(FormEditMode formEditMode);

	ResponsiveFormSection addEmptyFormSection();

	ResponsiveFormSection addFormSection(Icon<?, ?> icon, String title);

	ResponsiveFormLayout.LabelAndField addField(String label, AbstractField<?> field);

	ResponsiveFormLayout.LabelAndField addField(String label, Component component);

	FormPanel addTable(Table<?> table, Icon<?, ?> icon, String caption);

	<TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field);

	<TABLE_ENTITY> ListTable<TABLE_ENTITY> addSingleColumnTable(Icon<?, ?> icon, String caption, int rowHeight, AbstractField<TABLE_ENTITY> field, Function<TABLE_ENTITY, String> entityStringFunction);

	void addFields(List<AbstractField<?>> fields);

	void addMetaFields();

	void markAllFieldsUnchanged();

	void markFieldChanged(AbstractField<?> field);

	void handleEntityModified();

	void handleEntityModified(AbstractField<?> field);

	void handleNewEntityRequest();

	void handleSaveRequest();

	void handleSaveRequest(Function<ENTITY, Boolean> privilegeFunction);

	void handleRevertChangesRequest();

	void handleDeleteRequest();

	void handleRestoreRequest();

	void addButton(FormButton<ENTITY> formButton);

	FormButton<ENTITY> addNewEntityButton();

	FormButton<ENTITY> addNewEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addEditButton();

	FormButton<ENTITY> addEditButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addSaveEntityButton();

	FormButton<ENTITY> addSaveEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addRevertChangesButton();

	FormButton<ENTITY> addRevertChangesButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addDeleteEntityButton();

	FormButton<ENTITY> addDeleteEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addRestoreEntityButton();

	FormButton<ENTITY> addRestoreEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addEntityVersionsViewButton();

	FormButton<ENTITY> addEntityVersionsViewButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addEntityVersionsViewButton(EntityChangeHistoryView<ENTITY> changeHistoryView);

	FormButton<ENTITY> addShowPreviousEntityButton();

	FormButton<ENTITY> addShowPreviousEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addShowNextEntityButton();

	FormButton<ENTITY> addShowNextEntityButton(Icon<?, ?> icon, String caption, String description);

	FormButton<ENTITY> addToolsButton();

	FormButton<ENTITY> addToolsButton(Icon<?, ?> icon, String caption, String description);

	void addStandardFormButtons();

	void addAllButtons();

	static Icon<?, ?> createCompositeAddIcon(Icon<?, ?> icon) {
		return CompositeIcon.of(icon, ApplicationIcons.ADD);
	}

	static Icon<?, ?> createCompositeEditIcon(Icon<?, ?> icon) {
		return CompositeIcon.of(icon, ApplicationIcons.EDIT);
	}

	static Icon<?, ?> createCompositeDeleteIcon(Icon<?, ?> icon) {
		return CompositeIcon.of(icon, ApplicationIcons.ERROR);
	}
}
