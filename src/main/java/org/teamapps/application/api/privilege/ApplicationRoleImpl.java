/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
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

import org.teamapps.icons.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationRoleImpl implements ApplicationRole {

	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;
	private List<PrivilegeGroup> privilegeGroups;

	public ApplicationRoleImpl(String name, Icon icon, String titleKey, String descriptionKey, List<PrivilegeGroup> privilegeGroups) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
		this.privilegeGroups = privilegeGroups;
	}

	@Override
	public void mergeApplicationRole(ApplicationRole role) {
		Map<String, PrivilegeGroup> groupMap = privilegeGroups.stream().collect(Collectors.toMap(PrivilegeGroup::getName, group -> group));
		List<PrivilegeGroup> updatedGroups = new ArrayList<>();
		Set<String> roleGroupSet = role.getPrivilegeGroups().stream().map(PrivilegeGroup::getName).collect(Collectors.toSet());
		privilegeGroups.stream().filter(g -> !roleGroupSet.contains(g.getName())).forEach(updatedGroups::add);
		for (PrivilegeGroup privilegeGroup : role.getPrivilegeGroups()) {
			PrivilegeGroup existingGroup = groupMap.get(privilegeGroup.getName());
			if (existingGroup != null) {
				PrivilegeGroup mergedGroup = PrivilegeGroup.mergeGroups(existingGroup, privilegeGroup);
				updatedGroups.add(mergedGroup);
			} else {
				updatedGroups.add(privilegeGroup);
			}
		}
		privilegeGroups = updatedGroups;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getTitleKey() {
		return titleKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public List<PrivilegeGroup> getPrivilegeGroups() {
		return privilegeGroups;
	}
}
