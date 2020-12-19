package org.teamapps.application.api.privilege;

import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.Icon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AbstractApplicationPrivileges {

	private static List<PrivilegeGroup> privilegeGroups = new ArrayList<>();

	public static SimplePrivilege LAUNCH_APPLICATION = addSimplePrivilege("org.teamapps.privilege.launchApplication", ApplicationIcons.WINDOW, Dictionary.LAUNCH_APPLICATION, Dictionary.SENTENCE_ALLOWS_AUSER_TO_LAUNCH_THE_APP__);

	protected static SimplePrivilege addSimplePrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		SimplePrivilege privilegeGroup = PrivilegeGroup.createSimplePrivilege(name, icon, titleKey, descriptionKey);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static SimpleOrganizationalPrivilege addSimpleOrganizationalPrivilege(String name, Icon icon, String titleKey, String descriptionKey) {
		SimpleOrganizationalPrivilege privilegeGroup = PrivilegeGroup.createSimpleOrganizationalPrivilege(name, icon, titleKey, descriptionKey);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static SimpleCustomObjectPrivilege addSimpleCustomObjectPrivilege(String name, Icon icon, String titleKey, String descriptionKey, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		SimpleCustomObjectPrivilege privilegeGroup = PrivilegeGroup.createSimpleCustomObjectPrivilege(name, icon, titleKey, descriptionKey, privilegeObjectsSupplier);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static StandardPrivilegeGroup addStandardPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		StandardPrivilegeGroup privilegeGroup = PrivilegeGroup.createStandardPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static OrganizationalPrivilegeGroup addOrganizationalPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		OrganizationalPrivilegeGroup privilegeGroup = PrivilegeGroup.createOrganizationalPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static CustomObjectPrivilegeGroup addCustomObjectPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		CustomObjectPrivilegeGroup privilegeGroup = PrivilegeGroup.createCustomObjectPrivilegeGroup(name, icon, titleKey, descriptionKey, privileges, privilegeObjectsSupplier);
		addPrivilege(privilegeGroup);
		return privilegeGroup;
	}

	protected static PrivilegeGroup addPrivilege(PrivilegeGroup privilegeGroup) {
		privilegeGroups.add(privilegeGroup);
		return privilegeGroup;
	}

	public static List<PrivilegeGroup> getPrivileges() {
		return privilegeGroups;
	}
}
