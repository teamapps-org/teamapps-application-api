package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;

public interface CustomObjectPrivilegeGroup extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.CUSTOM_OBJECT_PRIVILEGE_GROUP;
	}

}
