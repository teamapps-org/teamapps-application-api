package org.teamapps.application.api.privilege;

import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.icons.api.Icon;

public interface Privilege {

	Privilege CREATE = create(PrivilegeType.CREATE, "create", null, Dictionary.CREATE);
	Privilege READ = create(PrivilegeType.READ, "read", null, Dictionary.READ);
	Privilege UPDATE = create(PrivilegeType.UPDATE, "update", null, Dictionary.UPDATE);
	Privilege DELETE = create(PrivilegeType.DELETE, "delete", null, Dictionary.DELETE);
	Privilege EXECUTE = create(PrivilegeType.EXECUTE, "execute", null, Dictionary.EXECUTE);
	Privilege PRINT = create(PrivilegeType.PRINT, "print", null, Dictionary.PRINT);
	Privilege CUSTOM = create(PrivilegeType.CUSTOM, "custom", null, Dictionary.CUSTOM);

	static Privilege create(PrivilegeType privilegeType, String name, Icon icon, String titleKey) {
		return new PrivilegeImpl(privilegeType, name, icon, titleKey);
	}

	PrivilegeType getType();

	String getName();

	Icon getIcon();

	String getTitleKey();



}
