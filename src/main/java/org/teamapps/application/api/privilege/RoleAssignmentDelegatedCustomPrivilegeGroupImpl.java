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

import java.util.List;
import java.util.function.Function;

public class RoleAssignmentDelegatedCustomPrivilegeGroupImpl extends AbstractPrivilegeGroup implements RoleAssignmentDelegatedCustomPrivilegeGroup{

	private Function<Integer, PrivilegeObject> privilegeObjectByIdFunction;

	public RoleAssignmentDelegatedCustomPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction) {
		super(name, icon, titleKey, descriptionKey, privileges);
		this.privilegeObjectByIdFunction = privilegeObjectByIdFunction;
	}

	public RoleAssignmentDelegatedCustomPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		super(name, icon, titleKey, descriptionKey, privileges);
		this.privilegeObjectByIdFunction = privilegeObjectByIdFunction;
	}

	public RoleAssignmentDelegatedCustomPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		super(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privileges);
		this.privilegeObjectByIdFunction = privilegeObjectByIdFunction;
	}

	@Override
	public PrivilegeGroup createCopyWithPrivileges(Privilege... privileges) {
		return new RoleAssignmentDelegatedCustomPrivilegeGroupImpl(getName(), getIcon(), getTitleKey(), getDescriptionKey(), privilegeObjectByIdFunction, privileges);
	}

	@Override
	public PrivilegeObject getPrivilegeObjectById(int id) {
		return privilegeObjectByIdFunction.apply(id);
	}
}
