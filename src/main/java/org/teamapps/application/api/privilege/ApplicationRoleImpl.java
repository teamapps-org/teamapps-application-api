package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

import java.util.List;

public class ApplicationRoleImpl implements ApplicationRole {

	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;
	private final List<PrivilegeGroup> privilegeGroups;

	public ApplicationRoleImpl(String name, Icon icon, String titleKey, String descriptionKey, List<PrivilegeGroup> privilegeGroups) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
		this.privilegeGroups = privilegeGroups;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getTitleKey() {
		return titleKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public List<PrivilegeGroup> getPrivilegeGroups() {
		return privilegeGroups;
	}
}
