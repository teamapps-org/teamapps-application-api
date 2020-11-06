package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public interface OrganizationalPrivilegeGroup extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.ORGANIZATIONAL_PRIVILEGE_GROUP;
	}

	@Override
	default Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier() {
		return null;
	}
}
