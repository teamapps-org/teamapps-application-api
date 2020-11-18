package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.List;

public class OrganizationalPrivilegeGroupImpl extends AbstractPrivilegeGroup implements OrganizationalPrivilegeGroup{

	public OrganizationalPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges) {
		super(name, icon, titleKey, descriptionKey, privileges);
	}

	public OrganizationalPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		super(name, icon, titleKey, descriptionKey, privileges);
	}
}
