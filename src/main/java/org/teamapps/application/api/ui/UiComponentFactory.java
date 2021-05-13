package org.teamapps.application.api.ui;

import org.teamapps.application.ux.localize.TranslatableField;
import org.teamapps.model.controlcenter.OrganizationUnitTypeView;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.field.combobox.TagComboBox;

import java.util.Set;

public interface UiComponentFactory {

	ComboBox<OrganizationUnitView> createOrganizationUnitComboBox(Set<OrganizationUnitView> allowedUnits);

	TagComboBox<OrganizationUnitTypeView> createOrganizationUnitTypeTagComboBox();

	TemplateField<OrganizationUnitView> createOrganizationUnitTemplateField();

	TemplateField<Integer> createUserTemplateField();

	TranslatableField createTranslatableField();

	TranslationKeyField createTranslationKeyField(String linkButtonCaption);

	FormMetaFields createFormMetaFields();

	String createUserAvatarLink(int userId, boolean large);

}
