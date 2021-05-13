package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.api.ui.FormMetaFields;
import org.teamapps.application.api.ui.TranslationKeyField;
import org.teamapps.application.api.ui.UiComponentFactory;
import org.teamapps.application.ux.UiUtils;
import org.teamapps.application.ux.localize.TranslatableField;
import org.teamapps.application.ux.org.OrganizationViewUtils;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.model.controlcenter.OrganizationUnitTypeView;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldEditingMode;
import org.teamapps.ux.component.field.TemplateField;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.field.combobox.TagComboBox;
import org.teamapps.ux.component.linkbutton.LinkButton;
import org.teamapps.ux.component.template.BaseTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DevUiComponentFactory implements UiComponentFactory {

	private final ApplicationInstanceData applicationInstanceData;

	public DevUiComponentFactory(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;
	}

	@Override
	public ComboBox<OrganizationUnitView> createOrganizationUnitComboBox(Set<OrganizationUnitView> allowedUnits) {
		return OrganizationViewUtils.createOrganizationComboBox(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE, allowedUnits, applicationInstanceData);
	}

	@Override
	public TagComboBox<OrganizationUnitTypeView> createOrganizationUnitTypeTagComboBox() {
		return OrganizationViewUtils.createOrganizationUnitTypeTagComboBox(150, applicationInstanceData);
	}

	@Override
	public TemplateField<OrganizationUnitView> createOrganizationUnitTemplateField() {
		return UiUtils.createTemplateField(BaseTemplate.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE, OrganizationViewUtils.creatOrganizationUnitViewPropertyProvider(applicationInstanceData));
	}

	@Override
	public TemplateField<Integer> createUserTemplateField() {
		return UiUtils.createTemplateField(BaseTemplate.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE, createUserIdPropertyProvider());
	}

	@Override
	public TranslatableField createTranslatableField() {
		return new TranslatableField(applicationInstanceData);
	}

	@Override
	public TranslationKeyField createTranslationKeyField(String linkButtonCaption) {
		final TextField selectionField = new TextField();
		final TextField keyField = new TextField();
		keyField.setEditingMode(FieldEditingMode.READONLY);
		return new TranslationKeyField() {
			@Override
			public AbstractField<String> getSelectionField() {
				return selectionField;
			}

			@Override
			public AbstractField<String> getKeyDisplayField() {
				return keyField;
			}

			@Override
			public LinkButton getKeyLinkButton() {
				return new LinkButton(linkButtonCaption);
			}

			@Override
			public void setKey(String key) {
				keyField.setValue(key);
				selectionField.setValue(key);
			}

			@Override
			public String getKey() {
				return selectionField.getValue();
			}
		};
	}

	@Override
	public FormMetaFields createFormMetaFields() {
		return null;
	}

	@Override
	public String createUserAvatarLink(int userId, boolean large) {
		return null;
	}

	public static PropertyProvider<Integer> createUserIdPropertyProvider() {
		return (userId, propertyNames) -> {
			Map<String, Object> map = new HashMap<>();
			map.put(BaseTemplate.PROPERTY_ICON, ApplicationIcons.USER);
			map.put(BaseTemplate.PROPERTY_CAPTION, "User with id:" + userId);
			return map;
		};
	}
}