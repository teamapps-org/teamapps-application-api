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

import org.teamapps.application.api.privilege.*;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public interface EntityPrivileges<ENTITY> {

	boolean isCreateAllowed();

	boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy);

	boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy);

	boolean isDeleteAllowed(ENTITY entity);

	boolean isRestoreAllowed(ENTITY entity);

	boolean isModificationHistoryAllowed(ENTITY entity);

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new OrgEntityPrivileges<>(organizationalPrivilegeGroup, unitByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		return new SimpleOrgEntityPrivileges<>(simpleOrganizationalPrivilege, unitByEntityFunction, privilegeProvider, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(StandardPrivilegeGroup standardPrivilegeGroup, ApplicationPrivilegeProvider privilegeProvider) {
		return new StandardEntityPrivileges<>(standardPrivilegeGroup, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleCustomObjectPrivilege simpleCustomObjectPrivilege, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		return new SimpleCustomEntityPrivileges<>(simpleCustomObjectPrivilege, privilegObjectByEntityFunction, privilegeProvider, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(CustomObjectPrivilegeGroup customObjectPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new CustomObjectEntityPrivileges<>(customObjectPrivilegeGroup, privilegObjectByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(RoleAssignmentDelegatedCustomPrivilegeGroup roleAssignmentDelegatedCustomPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new RoleAssignmentDelegatedCustomEntityPrivileges<>(roleAssignmentDelegatedCustomPrivilegeGroup, privilegObjectByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(EntityPrivileges<ENTITY>... entityPrivileges) {
		return new OrChainedEntityPrivileges<>(entityPrivileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> createAllowing(Privilege ... privileges) {
		return new FixedEntityPrivileges<>(privileges);
	}
}
