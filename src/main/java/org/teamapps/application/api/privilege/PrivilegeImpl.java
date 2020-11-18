package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

public class PrivilegeImpl implements Privilege {

	private final PrivilegeType privilegeType;
	private final String name;
	private final Icon icon;
	private final String titleKey;

	public PrivilegeImpl(PrivilegeType privilegeType, String name, Icon icon, String titleKey) {
		this.privilegeType = privilegeType;
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
	}

	@Override
	public PrivilegeType getType() {
		return privilegeType;
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
}
