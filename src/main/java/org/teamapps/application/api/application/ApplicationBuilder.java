package org.teamapps.application.api.application;

import org.teamapps.application.api.config.ApplicationConfig;
import org.teamapps.application.api.localization.LocalizationData;
import org.teamapps.application.api.privilege.ApplicationRole;
import org.teamapps.application.api.privilege.PrivilegeGroup;
import org.teamapps.application.api.versioning.ApplicationVersion;
import org.teamapps.icons.Icon;
import org.teamapps.universaldb.schema.SchemaInfoProvider;

import java.util.List;

public interface ApplicationBuilder {

	ApplicationVersion getApplicationVersion();

	Icon getApplicationIcon();

	String getApplicationName();

	String getApplicationTitleKey();

	String getApplicationDescriptionKey();

	List<ApplicationRole> getApplicationRoles();

	List<PrivilegeGroup> getPrivilegeGroups();

	LocalizationData getLocalizationData();

	SchemaInfoProvider getDatabaseModel();

	ApplicationConfig getApplicationConfig();

	void bootstrapApplicationBuilder();

	boolean isApplicationAccessible(ApplicationInstanceData applicationInstanceData);

	Application build(ApplicationInstanceData applicationInstanceData);

}
