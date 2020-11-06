package org.teamapps.application.api.privilege;

import org.teamapps.application.api.organization.OrgUnit;

import java.util.List;

public interface ApplicationPrivilegeProvider {

	boolean isAllowed(SimplePrivilege simplePrivilege);

	boolean isAllowed(SimpleOrganizationalPrivilege group, OrgUnit orgUnit);

	boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject);

	boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege);

	boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrgUnit orgUnit);

	boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject);

	List<OrgUnit> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege);

	List<OrgUnit> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege);

	List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege);

	List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege);

}
