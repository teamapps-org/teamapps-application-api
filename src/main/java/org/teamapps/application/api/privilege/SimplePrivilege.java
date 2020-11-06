package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public interface SimplePrivilege extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.SIMPLE_PRIVILEGE;
	}

	@Override
	default List<Privilege> getPrivileges() {
		return Collections.emptyList();
	}

	@Override
	default Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier() {
		return null;
	}
}
