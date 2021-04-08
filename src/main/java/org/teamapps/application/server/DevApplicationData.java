/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationBuilder;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.config.ApplicationConfig;
import org.teamapps.application.api.desktop.ApplicationDesktop;
import org.teamapps.application.api.localization.LocalizationData;
import org.teamapps.application.api.privilege.*;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.icons.Icon;
import org.teamapps.model.controlcenter.OrganizationFieldView;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.reporting.convert.DocumentConverter;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.progress.MultiProgressDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevApplicationData implements ApplicationInstanceData {

	private final ApplicationBuilder applicationBuilder;
	private final List<OrganizationUnitView> OrganizationUnitViews;
	private final DocumentConverter documentConverter;
	private final ResponsiveApplication responsiveApplication;
	private final Map<String, Map<String, String>> localizationMap;
	private final Map<String, Map<String, String>> dictionaryMap;

	public DevApplicationData(ApplicationBuilder applicationBuilder, List<OrganizationUnitView> OrganizationUnitViews, DocumentConverter documentConverter, ResponsiveApplication responsiveApplication) {
		this.applicationBuilder = applicationBuilder;
		this.localizationMap = applicationBuilder.getLocalizationData() != null ? applicationBuilder.getLocalizationData().createLocalizationMapByLanguage() : new HashMap<>();
		this.OrganizationUnitViews = OrganizationUnitViews;
		this.documentConverter = documentConverter;
		this.responsiveApplication = responsiveApplication;
		dictionaryMap = LocalizationData.createDictionaryData(getClass().getClassLoader()).createLocalizationMapByLanguage();
	}

	@Override
	public SessionUser getUser() {
		return null;
	}

	@Override
	public OrganizationFieldView getOrganizationField() {
		return null;
	}

	@Override
	public int getManagedApplicationId() {
		return 0;
	}

	@Override
	public DocumentConverter getDocumentConverter() {
		return documentConverter;
	}

	@Override
	public MultiProgressDisplay getMultiProgressDisplay() {
		return responsiveApplication.getMultiProgressDisplay();
	}

	@Override
	public void showPerspective(Perspective perspective) {
		responsiveApplication.showPerspective(perspective);
	}

	@Override
	public ApplicationDesktop createApplicationDesktop(Icon icon, String title, boolean select, boolean closable) {
		//todo
		return null;
	}

	@Override
	public ApplicationConfig<?> getApplicationConfig() {
		return applicationBuilder.getApplicationConfig();
	}

	@Override
	public void writeActivityLog(String eventTitle, String eventData) {
		System.out.println("User activity: " + eventTitle + ", " + eventData);
	}

	@Override
	public void writeExceptionLog(String title, Throwable throwable) {
		System.out.println("Exception: " + title + ", " + throwable.getMessage());
	}

	@Override
	public String getLocalized(String key, Object... parameters) {
		for (Map<String, String> map : localizationMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		for (Map<String, String> map : dictionaryMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return key;
	}

	@Override
	public String getLocalized(TranslatableText translatableText) {
		if (translatableText == null) {
			return null;
		}
		return translatableText.getText();
	}

	@Override
	public boolean isAllowed(SimplePrivilege simplePrivilege) {
		return true;
	}

	@Override
	public boolean isAllowed(SimpleOrganizationalPrivilege group, OrganizationUnitView OrganizationUnitView) {
		return true;
	}

	@Override
	public boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject) {
		return true;
	}

	@Override
	public boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege) {
		return true;
	}

	@Override
	public boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrganizationUnitView OrganizationUnitView) {
		return true;
	}

	@Override
	public boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
		return true;
	}

	@Override
	public List<OrganizationUnitView> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege) {
		return OrganizationUnitViews;
	}

	@Override
	public List<OrganizationUnitView> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege) {
		return OrganizationUnitViews;
	}

	@Override
	public List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege) {
		return simplePrivilege.getPrivilegeObjectsSupplier().get();
	}

	@Override
	public List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege) {
		return group.getPrivilegeObjectsSupplier().get();
	}

}
