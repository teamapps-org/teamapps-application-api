package org.teamapps.application.api.application;

import org.teamapps.application.api.privilege.PrivilegeObject;
import org.teamapps.model.controlcenter.OrganizationUnitView;

import java.util.List;

public interface ApplicationRoleAssignmentPrivilegeObjectProvider {

	String getPrivilegeObjectEntityTitleLocalizationKey();

	PrivilegeObject getPrivilegeObjectById(int id);

	List<PrivilegeObject> getPrivilegeObjects(OrganizationUnitView organizationUnit);

}
