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
package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.privilege.CustomObjectPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.PrivilegeObject;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class CustomObjectEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final CustomObjectPrivilegeGroup customObjectPrivilegeGroup;
	private final Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction;
	private final ApplicationPrivilegeProvider privilegeProvider;

	public CustomObjectEntityPrivileges(CustomObjectPrivilegeGroup customObjectPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		this.customObjectPrivilegeGroup = customObjectPrivilegeGroup;
		this.privilegObjectByEntityFunction = privilegObjectByEntityFunction;
		this.privilegeProvider = privilegeProvider;
	}

	@Override
	public boolean isCreateAllowed() {
		return !privilegeProvider.getAllowedPrivilegeObjects(customObjectPrivilegeGroup, Privilege.CREATE).isEmpty();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		if (entity.isStored()) {
			PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
			return privilegeProvider.isAllowed(customObjectPrivilegeGroup, Privilege.UPDATE, privilegeObject);
		} else {
			return !privilegeProvider.getAllowedPrivilegeObjects(customObjectPrivilegeGroup, Privilege.CREATE).isEmpty();
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(customObjectPrivilegeGroup, privilege, privilegeObject);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(customObjectPrivilegeGroup, Privilege.DELETE, privilegeObject);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(customObjectPrivilegeGroup, Privilege.RESTORE, privilegeObject);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(customObjectPrivilegeGroup, Privilege.SHOW_MODIFICATION_HISTORY, privilegeObject);
	}
}
