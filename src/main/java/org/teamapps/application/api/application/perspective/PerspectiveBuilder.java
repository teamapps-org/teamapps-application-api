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
package org.teamapps.application.api.application.perspective;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.perspective.ApplicationPerspective;
import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.Icon;
import org.teamapps.model.controlcenter.OrganizationFieldView;

public interface PerspectiveBuilder {

	Icon getIcon();

	String getName();

	String getTitleKey();

	String getDescriptionKey();

	default String getBadgeValue(ApplicationPrivilegeProvider privilegeProvider) {
		return null;
	}

	default int getApplicationBadgeCount(int userId, OrganizationFieldView organizationFieldView) {
		return 0;
	}

	boolean isPerspectiveAccessible(ApplicationPrivilegeProvider privilegeProvider);

	default boolean autoProvisionPerspective() {
		return true;
	}

	default boolean useToolbarPerspectiveMenu() {
		return false;
	}

	ApplicationPerspective build(ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue);

}
