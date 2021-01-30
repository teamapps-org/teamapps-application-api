/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.api.application;

import org.teamapps.application.api.organization.OrgUnit;
import org.teamapps.application.api.privilege.*;

import java.util.List;

public abstract class AbstractApplication implements Application {

	private final ApplicationInstanceData applicationInstanceData;

	public void writeActivityLog(String title, String data) {
		getApplicationInstanceData().writeActivityLog(title, data);
	}

	public void writeExceptionLog(String title, Throwable throwable) {
		getApplicationInstanceData().writeExceptionLog(title, throwable);
	}

	public AbstractApplication(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	public String getLocalized(String key, Object... parameters) {
		return applicationInstanceData.getLocalized(key, parameters);
	}

	public boolean isAllowed(SimplePrivilege simplePrivilege) {
		return applicationInstanceData.isAllowed(simplePrivilege);
	}

	public boolean isAllowed(SimpleOrganizationalPrivilege group, OrgUnit orgUnit) {
		return applicationInstanceData.isAllowed(group, orgUnit);
	}

	public boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject) {
		return applicationInstanceData.isAllowed(group, privilegeObject);
	}

	public boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.isAllowed(group, privilege);
	}

	public boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrgUnit orgUnit) {
		return applicationInstanceData.isAllowed(group, privilege, orgUnit);
	}

	public boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
		return applicationInstanceData.isAllowed(group, privilege, privilegeObject);
	}

	public List<OrgUnit> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege) {
		return applicationInstanceData.getAllowedUnits(simplePrivilege);
	}

	public List<OrgUnit> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.getAllowedUnits(group, privilege);
	}

	public List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege) {
		return applicationInstanceData.getAllowedPrivilegeObjects(simplePrivilege);
	}

	public List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.getAllowedPrivilegeObjects(group, privilege);
	}

}
