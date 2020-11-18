package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.List;
import java.util.function.Supplier;

public class CustomObjectPrivilegeGroupImpl extends AbstractPrivilegeGroup implements CustomObjectPrivilegeGroup{

	public CustomObjectPrivilegeGroupImpl(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		super(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
	}
}
