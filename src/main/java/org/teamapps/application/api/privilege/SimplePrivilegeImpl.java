package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

public class SimplePrivilegeImpl extends AbstractPrivilegeGroup implements SimplePrivilege{

	public SimplePrivilegeImpl(String name, Icon icon, String titleKey, String descriptionKey) {
		super(name, icon, titleKey, descriptionKey);
	}
}
