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
