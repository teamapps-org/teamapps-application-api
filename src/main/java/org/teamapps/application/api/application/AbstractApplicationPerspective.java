package org.teamapps.application.api.application;

import org.teamapps.application.api.organization.OrgUnit;
import org.teamapps.application.api.privilege.*;
import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.Component;

import java.util.List;

public abstract class AbstractApplicationPerspective implements ApplicationPerspective {

	private final ApplicationInstanceData applicationInstanceData;
	private final MutableValue<String> perspectiveInfoBadgeValue;

	private Component perspectiveMenuPanel;
	private Perspective perspective;

	public AbstractApplicationPerspective(ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue) {
		this.applicationInstanceData = applicationInstanceData;
		this.perspectiveInfoBadgeValue = perspectiveInfoBadgeValue;
		perspective = Perspective.createPerspective();
		createUi();
	}

	public abstract void createUi();

	public void setPerspectiveMenuPanel(Component perspectiveMenuPanel) {
		this.perspectiveMenuPanel = perspectiveMenuPanel;
	}

	public void setPerspective(Perspective perspective) {
		this.perspective = perspective;
	}

	@Override
	public Component getPerspectiveMenuPanel() {
		return perspectiveMenuPanel;
	}

	@Override
	public Perspective getPerspective() {
		return perspective;
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	public MutableValue<String> getPerspectiveInfoBadgeValue() {
		return perspectiveInfoBadgeValue;
	}

	public Icon getStyledIcon(Icon icon) {
		return applicationInstanceData.getStyledIcon(icon);
	}

	public Icon getStyledIcon(Icon icon, Icon subIcon) {
		return applicationInstanceData.getStyledIcon(subIcon);
	}

	public String getLocalized(String key, Object... parameters) {
		return applicationInstanceData.getLocalized(key, parameters);
	}

	public boolean isAllowed(SimplePrivilege simplePrivilege) {
		return applicationInstanceData.isAllowed(simplePrivilege);
	}

	public boolean isAllowed(SimpleOrganizationalPrivilege group, OrgUnit orgUnit) {
		return applicationInstanceData.isAllowed(group, orgUnit);
	}

	public boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject) {
		return applicationInstanceData.isAllowed(group, privilegeObject);
	}

	public boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.isAllowed(group, privilege);
	}

	public boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrgUnit orgUnit) {
		return applicationInstanceData.isAllowed(group, privilege, orgUnit);
	}

	public boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
		return applicationInstanceData.isAllowed(group, privilege, privilegeObject);
	}

	public List<OrgUnit> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege) {
		return applicationInstanceData.getAllowedUnits(simplePrivilege);
	}

	public List<OrgUnit> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.getAllowedUnits(group, privilege);
	}

	public List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege) {
		return applicationInstanceData.getAllowedPrivilegeObjects(simplePrivilege);
	}

	public List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege) {
		return applicationInstanceData.getAllowedPrivilegeObjects(group, privilege);
	}

}
