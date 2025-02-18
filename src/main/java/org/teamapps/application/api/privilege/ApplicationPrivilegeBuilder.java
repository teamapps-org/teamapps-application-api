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
package org.teamapps.application.api.privilege;

import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ApplicationPrivilegeBuilder {

	private List<PrivilegeGroup> privilegeGroups = new ArrayList<>();

	public SimplePrivilege LAUNCH_APPLICATION = addSimplePrivilege("org.teamapps.privilege.launchApplication", ApplicationIcons.WINDOW, Dictionary.LAUNCH_APPLICATION, Dictionary.SENTENCE_ALLOWS_AUSER_TO_LAUNCH_THE_APPLICAT__);

	public SimplePrivilege addSimplePrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		SimplePrivilege privilegeGroup = PrivilegeGroup.createSimplePrivilege(name, icon, titleKey, descriptionKey);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public SimpleOrganizationalPrivilege addSimpleOrganizationalPrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		SimpleOrganizationalPrivilege privilegeGroup = PrivilegeGroup.createSimpleOrganizationalPrivilege(name, icon, titleKey, descriptionKey);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public SimpleCustomObjectPrivilege addSimpleCustomObjectPrivilege(String name, Icon icon, String titleKey, String descriptionKey, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		SimpleCustomObjectPrivilege privilegeGroup = PrivilegeGroup.createSimpleCustomObjectPrivilege(name, icon, titleKey, descriptionKey, privilegeObjectsSupplier);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public StandardPrivilegeGroup addDefaultStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey) {
		return addStandardPrivilegeGroup(name, icon, titleKey, descriptionKey, Privilege.getDefault());
	}

	public StandardPrivilegeGroup addStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		StandardPrivilegeGroup privilegeGroup = PrivilegeGroup.createStandardPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public OrganizationalPrivilegeGroup addDefaultOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey) {
		return addOrganizationalPrivilegeGroup(name, icon, titleKey, descriptionKey, Privilege.getDefault());
	}

	public OrganizationalPrivilegeGroup addOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		OrganizationalPrivilegeGroup privilegeGroup = PrivilegeGroup.createOrganizationalPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public CustomObjectPrivilegeGroup addCustomObjectPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		CustomObjectPrivilegeGroup privilegeGroup = PrivilegeGroup.createCustomObjectPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	public RoleAssignmentDelegatedCustomPrivilegeGroup addDelegatedCustomPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		RoleAssignmentDelegatedCustomPrivilegeGroup customPrivilegeGroup = PrivilegeGroup.createDelegatedCustomPrivilegeGroup(name, icon, titleKey, descriptionKey, privilegeObjectByIdFunction, privileges);
		addPrivilege(customPrivilegeGroup);
		return customPrivilegeGroup;
	}

	private PrivilegeGroup addPrivilege(PrivilegeGroup privilegeGroup) {
		if (privilegeGroups.stream().anyMatch(group -> group.getName().equals(privilegeGroup.getName()))) {
			throw new RuntimeException("Duplicate privilege group name:" + privilegeGroup.getName());
		}
		privilegeGroups.add(privilegeGroup);
		return privilegeGroup;
	}

	public List<PrivilegeGroup> getPrivileges() {
		return privilegeGroups;
	}

	public void setCategory(String category, PrivilegeGroup... privilegeGroups) {
		for (PrivilegeGroup privilegeGroup : privilegeGroups) {
			if (privilegeGroup.getCategory() != null) {
				throw new RuntimeException("Cannot override privilege group category:" + privilegeGroup.getCategory());
			}
			privilegeGroup.setCategory(category);
		}

	}
}
