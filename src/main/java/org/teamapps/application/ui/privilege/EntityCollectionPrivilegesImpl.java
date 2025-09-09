package org.teamapps.application.ui.privilege;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.privilege.OrganizationalPrivilegeGroup;
import org.teamapps.application.api.privilege.Privilege;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.List;
import java.util.function.Function;

public class EntityCollectionPrivilegesImpl<ENTITY extends Entity<ENTITY>> implements EntityCollectionPrivileges<ENTITY> {

	private final OrganizationalPrivilegeGroup organizationalPrivilegeGroup;
	private final ApplicationInstanceData applicationInstanceData;
	private final EntityPrivileges<ENTITY> entityPrivileges;

	public EntityCollectionPrivilegesImpl(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.entityPrivileges = EntityPrivileges.create(organizationalPrivilegeGroup, unitByEntityFunction, applicationInstanceData);
		this.applicationInstanceData = applicationInstanceData;
	}

	public EntityCollectionPrivilegesImpl(OrganizationalPrivilegeGroup organizationalPrivilegeGroup, EntityPrivileges<ENTITY> entityPrivileges, ApplicationInstanceData applicationInstanceData) {
		this.organizationalPrivilegeGroup = organizationalPrivilegeGroup;
		this.entityPrivileges = entityPrivileges;
		this.applicationInstanceData = applicationInstanceData;
	}

	@Override
	public List<OrganizationUnitView> getReadAllowedOrgUnits() {
		return applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.READ);
	}

	@Override
	public List<OrganizationUnitView> getDeletedEntitiesAllowedOrgUnits() {
		return applicationInstanceData.getAllowedUnits(organizationalPrivilegeGroup, Privilege.DELETE);
	}

	@Override
	public boolean isCreateAllowed() {
		return entityPrivileges.isCreateAllowed();
	}

	@Override
	public boolean isSaveOptionAvailable(ENTITY entity) {
		return entityPrivileges.isSaveOptionAvailable(entity);
	}

	@Override
	public boolean isSaveAllowed(ENTITY entity) {
		return entityPrivileges.isSaveAllowed(entity);
	}

	@Override
	public boolean isDeleteAllowed(ENTITY entity) {
		return entityPrivileges.isDeleteAllowed(entity);
	}

	@Override
	public boolean isRestoreAllowed(ENTITY entity) {
		return entityPrivileges.isRestoreAllowed(entity);
	}

	@Override
	public boolean isModificationHistoryAllowed(ENTITY entity) {
		return entityPrivileges.isModificationHistoryAllowed(entity);
	}
}
