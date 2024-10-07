package org.teamapps.application.api.notification;

import org.teamapps.application.api.user.SessionUser;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

public interface ApplicationNotificationHandler {

	void sendEntityNotification(SessionUser sessionUser, Entity<?> entity, OrganizationUnitView organizationUnit, NotifiableAction action);


	void sendEntityNotification(SessionUser sessionUser, Entity<?> entity, OrganizationUnitView organizationUnit, int actionId);


	void sendEntityNotification(SessionUser sessionUser, Entity<?> entity, OrganizationUnitView organizationUnit, int actionId, int detailId);

	void sendEntityNotification(SessionUser sessionUser, Entity<?> entity, OrganizationUnitView organizationUnit, OrganizationUnitView moveToUnit, int actionId, int detailId);
}
