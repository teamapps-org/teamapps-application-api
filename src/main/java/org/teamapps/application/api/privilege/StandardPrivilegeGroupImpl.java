package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

import java.util.List;

public class StandardPrivilegeGroupImpl extends AbstractPrivilegeGroup implements StandardPrivilegeGroup{

	public StandardPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges) {
		super(name, icon, titleKey, descriptionKey, privileges);
	}

	public StandardPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		super(name, icon, titleKey, descriptionKey, privileges);
	}
}
