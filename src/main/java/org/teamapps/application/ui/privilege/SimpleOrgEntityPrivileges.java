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
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.SimpleOrganizationalPrivilege;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SimpleOrgEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final SimpleOrganizationalPrivilege simpleOrganizationalPrivilege;
	private final Function<ENTITY, OrganizationUnitView> unitByEntityFunction;
	private final Set<Privilege> privilegeSet;
	private final ApplicationPrivilegeProvider privilegeProvider;

	public SimpleOrgEntityPrivileges(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		this.simpleOrganizationalPrivilege = simpleOrganizationalPrivilege;
		this.unitByEntityFunction = unitByEntityFunction;
		this.privilegeSet = new HashSet<>(Arrays.asList(privileges));
		this.privilegeProvider = privilegeProvider;
	}

	@Override
	public boolean isCreateAllowed() {
		return isWithPrivilege(Privilege.CREATE);
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		if (entity.isStored()) {
			OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
			return isWithPrivilege(Privilege.UPDATE) && privilegeProvider.isAllowed(simpleOrganizationalPrivilege, orgUnit);
		} else {
			return isWithPrivilege(Privilege.CREATE);
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(privilege) && privilegeProvider.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.DELETE) && privilegeProvider.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.RESTORE) && privilegeProvider.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.SHOW_MODIFICATION_HISTORY) && privilegeProvider.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	private boolean isWithPrivilege(Privilege privilege) {
		return privilegeSet.contains(privilege);
	}
}
