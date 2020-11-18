package org.teamapps.application.api.application;

import org.teamapps.application.api.desktop.ApplicationDesktop;
import org.teamapps.application.api.localization.ApplicationLocalizationProvider;
import org.teamapps.application.api.organization.OrgField;
import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.icons.Icon;

public interface ApplicationInstanceData extends ApplicationPrivilegeProvider, ApplicationLocalizationProvider {

	SessionUser getUser();

	OrgField getOrganizationField();

	ApplicationDesktop createApplicationDesktop(Icon icon, String title, boolean select, boolean closable);
}
