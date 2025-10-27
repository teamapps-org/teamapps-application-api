package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.application.api.privilege.StandardPrivilegeGroup;
import org.teamapps.universaldb.pojo.Entity;

import java.util.HashSet;
import java.util.Set;

public class StandardEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final StandardPrivilegeGroup standardPrivilegeGroup;
	private final Set<Privilege> privilegeSet;

	public StandardEntityPrivileges(StandardPrivilegeGroup standardPrivilegeGroup, ApplicationPrivilegeProvider privilegeProvider) {
		this.standardPrivilegeGroup = standardPrivilegeGroup;
		this.privilegeSet = new HashSet<>();
		for (Privilege privilege : standardPrivilegeGroup.getPrivileges()) {
			if (privilegeProvider.isAllowed(standardPrivilegeGroup, privilege)) {
				privilegeSet.add(privilege);
			}
		}
	}

	@Override
	public boolean isCreateAllowed() {
		return privilegeSet.contains(Privilege.CREATE);
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy) {
		Privilege privilege = entity.isStored() ? Privilege.UPDATE : Privilege.CREATE;
		return privilegeSet.contains(privilege);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy) {
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
