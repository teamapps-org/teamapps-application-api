package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.application.ApplicationInstanceData;
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
	private final ApplicationInstanceData applicationInstanceData;

	public SimpleOrgEntityPrivileges(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData, Privilege ... privileges) {
		this.simpleOrganizationalPrivilege = simpleOrganizationalPrivilege;
		this.unitByEntityFunction = unitByEntityFunction;
		this.privilegeSet = new HashSet<>(Arrays.asList(privileges));
		this.applicationInstanceData = applicationInstanceData;
	}

	@Override
	public boolean isCreateAllowed() {
		return isWithPrivilege(Privilege.CREATE);
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		if (entity.isStored()) {
			OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
			return isWithPrivilege(Privilege.UPDATE) && applicationInstanceData.isAllowed(simpleOrganizationalPrivilege, orgUnit);
		} else {
			return isWithPrivilege(Privilege.CREATE);
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(privilege) && applicationInstanceData.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.DELETE) && applicationInstanceData.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.RESTORE) && applicationInstanceData.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		OrganizationUnitView orgUnit = unitByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.SHOW_MODIFICATION_HISTORY) && applicationInstanceData.isAllowed(simpleOrganizationalPrivilege, orgUnit);
	}

	private boolean isWithPrivilege(Privilege privilege) {
		return privilegeSet.contains(privilege);
	}
}
