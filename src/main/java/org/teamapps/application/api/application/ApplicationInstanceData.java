package org.teamapps.application.api.application;

import org.teamapps.application.api.localization.ApplicationLocalizationProvider;
import org.teamapps.application.api.organization.OrgField;
import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.theme.ApplicationThemeProvider;
import org.teamapps.application.api.user.SessionUser;

public interface ApplicationInstanceData extends ApplicationPrivilegeProvider, ApplicationThemeProvider, ApplicationLocalizationProvider {

	SessionUser getUser();

	OrgField getOrganizationField();

}
