package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

import java.util.List;
import java.util.function.Supplier;

public class SimpleCustomObjectPrivilegeImpl extends AbstractPrivilegeGroup implements SimpleCustomObjectPrivilege{

	public SimpleCustomObjectPrivilegeImpl(String name, Icon icon, String titleKey, String descriptionKey, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		super(name, icon, titleKey, descriptionKey, null, privilegeObjectsSupplier);
	}
}
