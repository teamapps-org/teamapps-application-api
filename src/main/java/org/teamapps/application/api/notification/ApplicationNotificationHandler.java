package org.teamapps.application.api.notification;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

public interface ApplicationNotificationHandler {

	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, NotifiableAction action, ApplicationInstanceData applicationInstanceData);


	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, int actionId, ApplicationInstanceData applicationInstanceData);


	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, int actionId, int detailId, ApplicationInstanceData applicationInstanceData);

	void sendEntityNotification(Entity<?> entity, OrganizationUnitView organizationUnit, OrganizationUnitView moveToUnit, int actionId, int detailId, ApplicationInstanceData applicationInstanceData);
}
