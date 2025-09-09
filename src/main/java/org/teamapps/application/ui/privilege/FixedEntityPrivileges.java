package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.universaldb.pojo.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FixedEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final Set<Privilege> privilegeSet;

	public FixedEntityPrivileges(Privilege... privileges) {
		this.privilegeSet = new HashSet<>(Arrays.asList(privileges));
	}

	@Override
	public boolean isCreateAllowed() {
		return privilegeSet.contains(Privilege.CREATE);
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		return privilegeSet.contains(privilege);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		return privilegeSet.contains(privilege);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		return privilegeSet.contains(Privilege.DELETE);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		return privilegeSet.contains(Privilege.RESTORE);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		return privilegeSet.contains(Privilege.SHOW_MODIFICATION_HISTORY);
	}
}
