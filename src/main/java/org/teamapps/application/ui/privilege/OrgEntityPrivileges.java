package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class OrgEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private final Function<ENTITY, OrganizationUnitView> unitByEntityFunction;
	private final ApplicationInstanceData applicationInstanceData;

	public OrgEntityPrivileges(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.unitByEntityFunction = unitByEntityFunction;
		this.applicationInstanceData = applicationInstanceData;
	}

	@Override
	public boolean isCreateAllowed() {
		return !applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		if (entity.isStored()) {
			OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
			return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, Privilege.UPDATE, orgUnit);
		} else {
			return !applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, privilege, orgUnit);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, Privilege.DELETE, orgUnit);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, Privilege.RESTORE, orgUnit);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(organizationalPrivilegeGroup, Privilege.SHOW_MODIFICATION_HISTORY, orgUnit);
	}
}
