package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

public class SimpleOrganizationalPrivilegeImpl extends AbstractPrivilegeGroup implements SimpleOrganizationalPrivilege{

	public SimpleOrganizationalPrivilegeImpl(String name, Icon icon, String titleKey, String descriptionKey) {
		super(name, icon, titleKey, descriptionKey);
	}
}
