package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.privilege.*;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public interface EntityPrivileges<ENTITY> {

	boolean isCreateAllowed();

	boolean isSaveOptionAvailable(ENTITY entity, ENTITY synchronizedEntityCopy);

	boolean isSaveAllowed(ENTITY entity, ENTITY synchronizedEntityCopy);

	boolean isDeleteAllowed(ENTITY entity);

	boolean isRestoreAllowed(ENTITY entity);

	boolean isModificationHistoryAllowed(ENTITY entity);

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new OrgEntityPrivileges<>(organizationalPrivilegeGroup, unitByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		return new SimpleOrgEntityPrivileges<>(simpleOrganizationalPrivilege, unitByEntityFunction, privilegeProvider, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(StandardPrivilegeGroup standardPrivilegeGroup, ApplicationPrivilegeProvider privilegeProvider) {
		return new StandardEntityPrivileges<>(standardPrivilegeGroup, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleCustomObjectPrivilege simpleCustomObjectPrivilege, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider, Privilege ... privileges) {
		return new SimpleCustomEntityPrivileges<>(simpleCustomObjectPrivilege, privilegObjectByEntityFunction, privilegeProvider, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(CustomObjectPrivilegeGroup customObjectPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new CustomObjectEntityPrivileges<>(customObjectPrivilegeGroup, privilegObjectByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(RoleAssignmentDelegatedCustomPrivilegeGroup roleAssignmentDelegatedCustomPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationPrivilegeProvider privilegeProvider) {
		return new RoleAssignmentDelegatedCustomEntityPrivileges<>(roleAssignmentDelegatedCustomPrivilegeGroup, privilegObjectByEntityFunction, privilegeProvider);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(EntityPrivileges<ENTITY>... entityPrivileges) {
		return new OrChainedEntityPrivileges<>(entityPrivileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> createAllowing(Privilege ... privileges) {
		return new FixedEntityPrivileges<>(privileges);
	}
}
