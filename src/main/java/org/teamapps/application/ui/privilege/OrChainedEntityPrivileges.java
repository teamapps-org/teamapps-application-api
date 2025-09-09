package org.teamapps.application.ui.privilege;

import org.teamapps.universaldb.pojo.Entity;

import java.util.Arrays;
import java.util.List;

public class OrChainedEntityPrivileges<ENTITY extends Entity<ENTITY>> implements EntityPrivileges<ENTITY> {

	private final List<EntityPrivileges<ENTITY>> privileges;
	private EntityPrivileges<ENTITY> andPrivilege;

	public OrChainedEntityPrivileges(EntityPrivileges<ENTITY>... privileges) {
		this.privileges = Arrays.asList(privileges);
	}

	public OrChainedEntityPrivileges(List<EntityPrivileges<ENTITY>> privileges) {
		this.privileges = privileges;
	}

	public OrChainedEntityPrivileges(List<EntityPrivileges<ENTITY>> privileges, EntityPrivileges<ENTITY> andPrivilege) {
		this.privileges = privileges;
		this.andPrivilege = andPrivilege;
	}

	public OrChainedEntityPrivileges<ENTITY> setAndPrivilege(EntityPrivileges<ENTITY> andPrivilege) {
		this.andPrivilege = andPrivilege;
		return this;
	}

	@Override
	public boolean isCreateAllowed() {
		if (andPrivilege != null && !andPrivilege.isCreateAllowed()) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isCreateAllowed()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		if (andPrivilege != null && !andPrivilege.isSaveOptionAvailable(entity)) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isSaveOptionAvailable(entity)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		if (andPrivilege != null && !andPrivilege.isSaveAllowed(entity)) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isSaveAllowed(entity)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		if (andPrivilege != null && !andPrivilege.isDeleteAllowed(entity)) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isDeleteAllowed(entity)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		if (andPrivilege != null && !andPrivilege.isRestoreAllowed(entity)) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isRestoreAllowed(entity)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		if (andPrivilege != null && !andPrivilege.isModificationHistoryAllowed(entity)) {
			return false;
		}
		for (EntityPrivileges<ENTITY> privilege : privileges) {
			if (privilege.isModificationHistoryAllowed(entity)) {
				return true;
			}
		}
		return false;
	}
}
