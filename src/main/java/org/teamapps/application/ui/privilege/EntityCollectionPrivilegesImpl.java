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
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.List;
import java.util.function.Function;

public class EntityCollectionPrivilegesImpl<ENTITY extends Entity<ENTITY>> implements EntityCollectionPrivileges<ENTITY> {

	private final OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private final ApplicationPrivilegeProvider privilegeProvider;
	private final EntityPrivileges<ENTITY> entityPrivileges;

	public EntityCollectionPrivilegesImpl(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.entityPrivileges = EntityPrivileges.create(organizationalPrivilegeGroup, unitByEntityFunction, privilegeProvider);
		this.privilegeProvider = privilegeProvider;
	}

	public EntityCollectionPrivilegesImpl(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, EntityPrivileges<ENTITY> entityPrivileges, ApplicationPrivilegeProvider privilegeProvider) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.entityPrivileges = entityPrivileges;
		this.privilegeProvider = privilegeProvider;
	}

	@Override
	public List<OrganizationUnitView> getReadAllowedOrgUnits() {
		return privilegeProvider.getAllowedUnits(organizationalPrivilegeGroup, Privilege.READ);
	}

	@Override
	public List<OrganizationUnitView> getDeletedEntitiesAllowedOrgUnits() {
		return privilegeProvider.getAllowedUnits(organizationalPrivilegeGroup, Privilege.DELETE);
	}

	@Override
	public boolean isCreateAllowed() {
		return entityPrivileges.isCreateAllowed();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		return entityPrivileges.isSaveOptionAvailable(entity, synchronizedEntityCopy);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		return entityPrivileges.isSaveAllowed(entity, synchronizedEntityCopy);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		return entityPrivileges.isDeleteAllowed(entity);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		return entityPrivileges.isRestoreAllowed(entity);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		return entityPrivileges.isModificationHistoryAllowed(entity);
	}
}
