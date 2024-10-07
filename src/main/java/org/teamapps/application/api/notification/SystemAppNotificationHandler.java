package org.teamapps.application.api.notification;

import org.teamapps.model.controlcenter.OrganizationFieldView;

public interface SystemAppNotificationHandler {

	ApplicationNotificationHandler getNotificationHandler(OrganizationFieldView organizationField);
}
