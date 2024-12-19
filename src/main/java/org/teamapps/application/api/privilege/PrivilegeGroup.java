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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PrivilegeGroup {


	static SimplePrivilege createSimplePrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		return new SimplePrivilegeImpl(name, icon, titleKey, descriptionKey);
	}

	static SimplePrivilege createSimplePrivilege(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden) {
		return new SimplePrivilegeImpl(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden);
	}

	static SimpleOrganizationalPrivilege createSimpleOrganizationalPrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		return new SimpleOrganizationalPrivilegeImpl(name, icon, titleKey, descriptionKey);
	}

	static SimpleOrganizationalPrivilege createSimpleOrganizationalPrivilege(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden) {
		return new SimpleOrganizationalPrivilegeImpl(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden);
	}

	static SimpleCustomObjectPrivilege createSimpleCustomObjectPrivilege(String name, Icon icon, String titleKey, String descriptionKey, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		return new SimpleCustomObjectPrivilegeImpl(name, icon, titleKey, descriptionKey, privilegeObjectsSupplier);
	}

	static StandardPrivilegeGroup createStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		return new StandardPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges);
	}

	static StandardPrivilegeGroup createStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, Privilege... privileges) {
		return new StandardPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privileges);
	}

	static OrganizationalPrivilegeGroup createOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		return new OrganizationalPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges);
	}

	static OrganizationalPrivilegeGroup createOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, Privilege... privileges) {
		return new OrganizationalPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privileges);
	}

	static CustomObjectPrivilegeGroup createCustomObjectPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		return new CustomObjectPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
	}

	static CustomObjectPrivilegeGroup createCustomObjectPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		return new CustomObjectPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privileges, privilegeObjectsSupplier);
	}

	static RoleAssignmentDelegatedCustomPrivilegeGroup createDelegatedCustomPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		return new RoleAssignmentDelegatedCustomPrivilegeGroupImpl(null, name, icon, titleKey, descriptionKey, privilegeObjectByIdFunction, privileges);
	}

	static RoleAssignmentDelegatedCustomPrivilegeGroup createDelegatedCustomPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		return new RoleAssignmentDelegatedCustomPrivilegeGroupImpl(null, name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privilegeObjectByIdFunction, privileges);
	}

	static RoleAssignmentDelegatedCustomPrivilegeGroup createDelegatedCustomPrivilegeGroup(String objectType, String name, Icon icon, String titleKey, String descriptionKey, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		return new RoleAssignmentDelegatedCustomPrivilegeGroupImpl(objectType, name, icon, titleKey, descriptionKey, privilegeObjectByIdFunction, privileges);
	}

	static RoleAssignmentDelegatedCustomPrivilegeGroup createDelegatedCustomPrivilegeGroup(String objectType, String name, Icon icon, String titleKey, String descriptionKey, boolean multiFactorAuthenticationRequired, boolean inheritanceForbidden, Function<Integer, PrivilegeObject> privilegeObjectByIdFunction, Privilege... privileges) {
		return new RoleAssignmentDelegatedCustomPrivilegeGroupImpl(objectType, name, icon, titleKey, descriptionKey, multiFactorAuthenticationRequired, inheritanceForbidden, privilegeObjectByIdFunction, privileges);
	}

	static PrivilegeGroup mergeGroups(PrivilegeGroup groupA, PrivilegeGroup groupB) {
		if (groupA.getType() != groupB.getType()) {
			throw new RuntimeException("Cannot merge privilege groups of different type:" + groupA);
		}
		List<Privilege> privileges = groupA.getPrivileges();
		Set<Privilege> privilegeSet = new HashSet<>(privileges);
		groupB.getPrivileges().stream().filter(p -> !privilegeSet.contains(p)).forEach(privileges::add);
		return groupA.createCopyWithPrivileges(privileges.toArray(new Privilege[0]));
	}

	PrivilegeGroup createCopyWithPrivileges(Privilege... privileges);

	PrivilegeGroupType getType();

	String getName();

	Icon getIcon();

	String getTitleKey();

	String getDescriptionKey();

	boolean isMultiFactorAuthenticationRequired();

	boolean isInheritanceForbidden();

	List<Privilege> getPrivileges();

	Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier();

	String getCategory();

	void setCategory(String category);
}
