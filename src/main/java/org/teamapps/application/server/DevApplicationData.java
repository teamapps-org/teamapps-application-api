package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationBuilder;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.desktop.ApplicationDesktop;
import org.teamapps.application.api.localization.LocalizationData;
import org.teamapps.application.api.organization.OrgField;
import org.teamapps.application.api.organization.OrgUnit;
import org.teamapps.application.api.privilege.*;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.icons.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevApplicationData implements ApplicationInstanceData {

	private final ApplicationBuilder applicationBuilder;
	private final List<OrgUnit> orgUnits;
	private final Map<String, Map<String, String>> localizationMap;
	private final Map<String, Map<String, String>> dictionaryMap;

	public DevApplicationData(ApplicationBuilder applicationBuilder, List<OrgUnit> orgUnits) {
		this.applicationBuilder = applicationBuilder;
		this.localizationMap = applicationBuilder.getLocalizationData() != null ? applicationBuilder.getLocalizationData().createLocalizationMap() : new HashMap<>();
		this.orgUnits = orgUnits;
		dictionaryMap = LocalizationData.createDictionaryData().createLocalizationMap();
	}

	@Override
	public SessionUser getUser() {
		return null;
	}

	@Override
	public OrgField getOrganizationField() {
		return null;
	}

	@Override
	public ApplicationDesktop createApplicationDesktop(Icon icon, String title, boolean select, boolean closable) {
		//todo
		return null;
	}

	@Override
	public String getLocalized(String key, Object... parameters) {
		for (Map<String, String> map : localizationMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		for (Map<String, String> map : dictionaryMap.values()) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return key;
	}

	@Override
	public boolean isAllowed(SimplePrivilege simplePrivilege) {
		return true;
	}

	@Override
	public boolean isAllowed(SimpleOrganizationalPrivilege group, OrgUnit orgUnit) {
		return true;
	}

	@Override
	public boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject) {
		return true;
	}

	@Override
	public boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege) {
		return true;
	}

	@Override
	public boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrgUnit orgUnit) {
		return true;
	}

	@Override
	public boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
		return true;
	}

	@Override
	public List<OrgUnit> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege) {
		return orgUnits;
	}

	@Override
	public List<OrgUnit> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege) {
		return orgUnits;
	}

	@Override
	public List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege) {
		return simplePrivilege.getPrivilegeObjectsSupplier().get();
	}

	@Override
	public List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege) {
		return group.getPrivilegeObjectsSupplier().get();
	}

}
