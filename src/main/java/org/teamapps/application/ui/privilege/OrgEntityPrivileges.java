package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class OrgEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private final Function<ENTITY, OrganizationUnitView> unitByEntityFunction;
	private final ApplicationPrivilegeProvider privilegeProvider;

	public OrgEntityPrivileges(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider applicationInstanceData) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.unitByEntityFunction = unitByEntityFunction;
		this.privilegeProvider = applicationInstanceData;
	}


	@Override
	public boolean isCreateAllowed() {
		return !privilegeProvider.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		if (entity.isStored()) {
			OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
			return privilegeProvider.isAllowed(organizationalPrivilegeGroup, Privilege.UPDATE, orgUnit);
		} else {
			return !privilegeProvider.getAllowedUnits(organizationalPrivilegeGroup, Privilege.CREATE).isEmpty();
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(organizationalPrivilegeGroup, privilege, orgUnit);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(organizationalPrivilegeGroup, Privilege.DELETE, orgUnit);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(organizationalPrivilegeGroup, Privilege.RESTORE, orgUnit);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return privilegeProvider.isAllowed(organizationalPrivilegeGroup, Privilege.SHOW_MODIFICATION_HISTORY, orgUnit);
	}
}
