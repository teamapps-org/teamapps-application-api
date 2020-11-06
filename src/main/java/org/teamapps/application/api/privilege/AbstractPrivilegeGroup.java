package org.teamapps.application.api.privilege;

import org.teamapps.icons.api.Icon;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractPrivilegeGroup implements PrivilegeGroup {

	private final String name;
	private final Icon icon;
	private final String titleKey;
	private final String descriptionKey;
	private List<Privilege> privileges;
	private Supplier<List<PrivilegeObject>> privilegeObjectsSupplier;

	public AbstractPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
	}

	public AbstractPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
		this.privileges = privileges;
	}

	public AbstractPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, Privilege... privileges) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
		this.privileges = Arrays.asList(privileges);
	}

	public AbstractPrivilegeGroup(String name, Icon icon, String titleKey, String descriptionKey, List<Privilege> privileges, Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		this.name = name;
		this.icon = icon;
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
		this.privileges = privileges;
		this.privilegeObjectsSupplier = privilegeObjectsSupplier;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public void setPrivilegeObjectsSupplier(Supplier<List<PrivilegeObject>> privilegeObjectsSupplier) {
		this.privilegeObjectsSupplier = privilegeObjectsSupplier;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getTitleKey() {
		return titleKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public List<Privilege> getPrivileges() {
		return privileges;
	}

	@Override
	public Supplier<List<PrivilegeObject>> getPrivilegeObjectsSupplier() {
		return privilegeObjectsSupplier;
	}
}
