package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;

public interface SimpleCustomObjectPrivilege extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.SIMPLE_CUSTOM_OBJECT_PRIVILEGE;
	}

	@Override
	default List<Privilege> getPrivileges() {
		return Collections.emptyList();
	}
}
