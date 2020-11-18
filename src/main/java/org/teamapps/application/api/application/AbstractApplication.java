package org.teamapps.application.api.application;

import org.teamapps.application.api.organization.OrgUnit;
import org.teamapps.application.api.privilege.*;
import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.Icon;

import java.util.List;

public abstract class AbstractApplication implements Application {

	private final ApplicationInstanceData applicationInstanceData;

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
