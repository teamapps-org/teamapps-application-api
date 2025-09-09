package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.privilege.*;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public interface EntityPrivileges<ENTITY> {

	boolean isCreateAllowed();

	boolean isSaveOptionAvailable(ENTITY entity);

	boolean isSaveAllowed(ENTITY entity);

	boolean isDeleteAllowed(ENTITY entity);

	boolean isRestoreAllowed(ENTITY entity);

	boolean isModificationHistoryAllowed(ENTITY entity);

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		return new OrgEntityPrivileges<>(organizationalPrivilegeGroup, unitByEntityFunction, applicationInstanceData);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleOrganizationalPrivilege simpleOrganizationalPrivilege, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData, Privilege ... privileges) {
		return new SimpleOrgEntityPrivileges<>(simpleOrganizationalPrivilege, unitByEntityFunction, applicationInstanceData, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(StandardPrivilegeGroup standardPrivilegeGroup, ApplicationInstanceData applicationInstanceData) {
		return new StandardEntityPrivileges<>(standardPrivilegeGroup, applicationInstanceData);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(SimpleCustomObjectPrivilege simpleCustomObjectPrivilege, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationInstanceData applicationInstanceData, Privilege ... privileges) {
		return new SimpleCustomEntityPrivileges<>(simpleCustomObjectPrivilege, privilegObjectByEntityFunction, applicationInstanceData, privileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(CustomObjectPrivilegeGroup customObjectPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		return new CustomObjectEntityPrivileges<>(customObjectPrivilegeGroup, privilegObjectByEntityFunction, applicationInstanceData);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(RoleAssignmentDelegatedCustomPrivilegeGroup roleAssignmentDelegatedCustomPrivilegeGroup, Function<ENTITY, PrivilegeObject> privilegObjectByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		return new RoleAssignmentDelegatedCustomEntityPrivileges<>(roleAssignmentDelegatedCustomPrivilegeGroup, privilegObjectByEntityFunction, applicationInstanceData);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> create(EntityPrivileges<ENTITY>... entityPrivileges) {
		return new OrChainedEntityPrivileges<>(entityPrivileges);
	}

	static <ENTITY extends Entity<ENTITY>> EntityPrivileges<ENTITY> createAllowing(Privilege ... privileges) {
		return new FixedEntityPrivileges<>(privileges);
	}
}
