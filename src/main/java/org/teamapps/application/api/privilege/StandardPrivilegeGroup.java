package org.teamapps.application.api.privilege;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public interface StandardPrivilegeGroup extends PrivilegeGroup {

	@Override
	default PrivilegeGroupType getType() {
		return PrivilegeGroupType.STANDARD_PRIVILEGE_GROUP;
	}

	@Override
	default Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier() {
		return null;
	}
}
