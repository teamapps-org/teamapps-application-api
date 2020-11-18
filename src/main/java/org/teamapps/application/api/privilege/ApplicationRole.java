package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.Arrays;
import java.util.List;

public interface ApplicationRole {

	static ApplicationRole createRole(String name, Icon icon, String titleKey, String descriptionKey, List<PrivilegeGroup> privilegeGroups) {
		return new ApplicationRoleImpl(name, icon, titleKey, descriptionKey, privilegeGroups);
	}

	static ApplicationRole createRole(String name, Icon icon, String titleKey, String descriptionKey, PrivilegeGroup... privilegeGroups) {
		return new ApplicationRoleImpl(name, icon, titleKey, descriptionKey, Arrays.asList(privilegeGroups));
	}

	String getName();

	Icon getIcon();

	String getTitleKey();

	String getDescriptionKey();

	List<PrivilegeGroup> getPrivilegeGroups();

}
