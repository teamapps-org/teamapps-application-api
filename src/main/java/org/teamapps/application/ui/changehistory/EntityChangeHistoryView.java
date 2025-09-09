package org.teamapps.application.ui.changehistory;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.application.ux.window.ApplicationWindow;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.ux.component.template.Template;

public interface EntityChangeHistoryView<ENTITY> {

	void addField(String fieldName, String fieldTitle);

	void addFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle);

	<REFERENCED_ENTITY> void addPropertyField(String fieldName, String fieldTitle, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	<REFERENCED_ENTITY> void addPropertyFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	<REFERENCED_ENTITY> void addEnumField(String fieldName, String fieldTitle, REFERENCED_ENTITY[] enumValues, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	<REFERENCED_ENTITY> void addEnumFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, REFERENCED_ENTITY[] enumValues, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	<REFERENCED_ENTITY> void addReferenceField(String fieldName, String fieldTitle, EntityBuilder<REFERENCED_ENTITY> entityBuilder, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	<REFERENCED_ENTITY> void addReferenceFieldFromReferencedEntity(String referenceFieldName, String fieldName, String fieldTitle, EntityBuilder<REFERENCED_ENTITY> entityBuilder, Template template, PropertyProvider<REFERENCED_ENTITY> propertyProvider);

	void show();

	static <ENTITY extends Entity<ENTITY>> void showVersionsWindow(EntityChangeHistoryView<ENTITY> view, ENTITY entity, ApplicationInstanceData applicationInstanceData) {
		EntityChangeHistoryViewImpl<ENTITY> changeHistoryView = new EntityChangeHistoryViewImpl<>(EntityChangeHistoryModel.createModelFromEntity(entity), applicationInstanceData);
		ApplicationWindow window = new ApplicationWindow(ApplicationIcons.CLOCK_BACK, applicationInstanceData.getLocalized(Dictionary.MODIFICATION_HISTORY), applicationInstanceData);
		changeHistoryView.setParentWindow(window.getWindow());
		changeHistoryView.show();
	}

}
