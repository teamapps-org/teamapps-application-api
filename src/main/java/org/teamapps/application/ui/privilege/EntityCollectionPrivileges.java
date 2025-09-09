package org.teamapps.application.ui.privilege;

import org.teamapps.model.controlcenter.OrganizationUnitView;

import java.util.List;

public interface EntityCollectionPrivileges<ENTITY> extends EntityPrivileges<ENTITY> {

	List<OrganizationUnitView> getReadAllowedOrgUnits();

	List<OrganizationUnitView> getDeletedEntitiesAllowedOrgUnits();


}
