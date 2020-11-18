package org.teamapps.application.api.privilege;

import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.icon.standard.StandardIcon;
import org.teamapps.icons.Icon;

import java.util.List;
import java.util.function.Supplier;

public interface PrivilegeGroup {

	SimplePrivilege LAUNCH_APPLICATION = createSimplePrivilege("org.teamapps.privilege.launchApplication", StandardIcon.GEARWHEEL, Dictionary.LAUNCH_APPLICATION, Dictionary.SENTENCE_ALLOWS_AUSER_TO_LAUNCH_THE_APP__);

	static SimplePrivilege createSimplePrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		return new SimplePrivilegeImpl(name, icon, titleKey, descriptionKey);
	}

	static SimpleOrganizationalPrivilege createSimpleOrganizationalPrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		return new SimpleOrganizationalPrivilegeImpl(name, icon, titleKey, descriptionKey);
	}

	static SimpleCustomObjectPrivilege createSimpleCustomObjectPrivilege(String name, Icon icon, String titleKey, String descriptionKey, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		return new SimpleCustomObjectPrivilegeImpl(name, icon, titleKey, descriptionKey, privilegeObjectsSupplier);
	}

	static StandardPrivilegeGroup createStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		return new StandardPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges);
	}

	static OrganizationalPrivilegeGroup createOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		return new OrganizationalPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges);
	}

	static CustomObjectPrivilegeGroup createCustomObjectPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		return new CustomObjectPrivilegeGroupImpl(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
	}

	PrivilegeGroupType getType();

	String getName();

	Icon getIcon();

	String getTitleKey();

	String getDescriptionKey();

	List<Privilege> getPrivileges();

	Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier();
}
