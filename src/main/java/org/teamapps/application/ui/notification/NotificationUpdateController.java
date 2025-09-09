package org.teamapps.application.ui.notification;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface NotificationUpdateController<ENTITY> {

	static <ENTITY extends Entity<ENTITY>> NotificationUpdateController<ENTITY> createNotificationUpdateController(LifecycleEntityModel<ENTITY> lifecycleEntityModel, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		return new NotificationUpdateControllerImpl<>(lifecycleEntityModel, unitByEntityFunction, applicationInstanceData);
	}

	void registerNotifyOnCreation();

	void registerNotifyOnCreation(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnUpdate();

	void registerNotifyOnUpdate(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnDeletion();

	void registerNotifyOnDeletion(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnRestore();

	void registerNotifyOnRestore(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnOrgMove();

	void registerNotifyOnOrgMove(Function<ENTITY, Integer> detailIdFunction);

	void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction);

	void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction, Function<ENTITY, Integer> detailIdFunction);

}
