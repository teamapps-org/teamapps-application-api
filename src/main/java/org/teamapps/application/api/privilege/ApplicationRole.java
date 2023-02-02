/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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

import java.util.Arrays;
import java.util.List;

public interface ApplicationRole {

	static ApplicationRole createRole(String name, Icon icon, String titleKey, String descriptionKey, List<PrivilegeGroup> privilegeGroups) {
		return new ApplicationRoleImpl(name, icon, titleKey, descriptionKey, privilegeGroups);
	}

	static ApplicationRole createRole(String name, Icon icon, String titleKey, String descriptionKey, PrivilegeGroup... privilegeGroups) {
		return new ApplicationRoleImpl(name, icon, titleKey, descriptionKey, Arrays.asList(privilegeGroups));
	}

	void mergeApplicationRole(ApplicationRole role);

	String getName();

	Icon getIcon();

	String getTitleKey();

	String getDescriptionKey();

	List<PrivilegeGroup> getPrivilegeGroups();

}
