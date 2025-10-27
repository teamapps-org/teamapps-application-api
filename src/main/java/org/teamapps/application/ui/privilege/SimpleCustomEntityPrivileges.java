package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.PrivilegeObject;
import org.teamapps.application.api.privilege.SimpleCustomObjectPrivilege;
import org.teamapps.universaldb.pojo.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SimpleCustomEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final SimpleCustomObjectPrivilege simpleCustomObjectPrivilege;
	private final Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction;
	private final Set<Privilege> privilegeSet;
	private final ApplicationPrivilegeProvider privilegeProvider;

	public SimpleCustomEntityPrivileges(SimpleCustomObjectPrivilege simpleCustomObjectPrivilege, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		this.simpleCustomObjectPrivilege = simpleCustomObjectPrivilege;
		this.privilegObjectByEntityFunction = privilegObjectByEntityFunction;
		this.privilegeSet = new HashSet<>(Arrays.asList(privileges));
		this.privilegeProvider = privilegeProvider;
	}

	@Override
	public boolean isCreateAllowed() {
		return isWithPrivilege(Privilege.CREATE);
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		if (entity.isStored()) {
			PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
			return isWithPrivilege(Privilege.UPDATE) && privilegeProvider.isAllowed(simpleCustomObjectPrivilege, privilegeObject);
		} else {
			return isWithPrivilege(Privilege.CREATE);
		}
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return isWithPrivilege(privilege) && privilegeProvider.isAllowed(simpleCustomObjectPrivilege, privilegeObject);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.DELETE) && privilegeProvider.isAllowed(simpleCustomObjectPrivilege, privilegeObject);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.RESTORE) && privilegeProvider.isAllowed(simpleCustomObjectPrivilege, privilegeObject);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		PrivilegeObject privilegeObject = privilegObjectByEntityFunction.apply(entity);
		return isWithPrivilege(Privilege.SHOW_MODIFICATION_HISTORY) && privilegeProvider.isAllowed(simpleCustomObjectPrivilege, privilegeObject);
	}

	private boolean isWithPrivilege(Privilege privilege) {
		return privilegeSet.contains(privilege);
	}
}
