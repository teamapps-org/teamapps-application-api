package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.privilege.CustomObjectPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.PrivilegeObject;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class CustomObjectEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final CustomObjectPrivilegeGroup customObjectPrivilegeGroup;
	private final Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction;
	private final ApplicationInstanceData applicationInstanceData;

	public CustomObjectEntityPrivileges(CustomObjectPrivilegeGroup customObjectPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		this.customObjectPrivilegeGroup = customObjectPrivilegeGroup;
		this.privilegObjectByEntityFunction = privilegObjectByEntityFunction;
		this.applicationInstanceData = applicationInstanceData;
	}

	@Override
	public boolean isCreateAllowed() {
		return !applicationInstanceData.getAllowedPrivilegeObjects(customObjectPrivilegeGroup, Privilege.CREATE).isEmpty();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		if (entity.isStored()) {
			PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
			return applicationInstanceData.isAllowed(customObjectPrivilegeGroup, Privilege.UPDATE, privilegeObject);
		} else {
			return !applicationInstanceData.getAllowedPrivilegeObjects(customObjectPrivilegeGroup, Privilege.CREATE).isEmpty();
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(customObjectPrivilegeGroup, privilege, privilegeObject);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(customObjectPrivilegeGroup, Privilege.DELETE, privilegeObject);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(customObjectPrivilegeGroup, Privilege.RESTORE, privilegeObject);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return applicationInstanceData.isAllowed(customObjectPrivilegeGroup, Privilege.SHOW_MODIFICATION_HISTORY, privilegeObject);
	}
}
