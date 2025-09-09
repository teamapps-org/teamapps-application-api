package org.teamapps.application.ui.notification;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.notification.ApplicationNotificationHandler;
import org.teamapps.application.api.notification.NotifiableAction;
import org.teamapps.application.ui.model.LifecycleEntityModel;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.pojo.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NotificationUpdateControllerImpl<ENTITY extends Entity<ENTITY>> implements NotificationUpdateController<ENTITY> {

	private final LifecycleEntityModel<ENTITY> lifecycleEntityModel;
	private final Function<ENTITY, OrganizationUnitView> unitByEntityFunction;
	private final ApplicationInstanceData applicationInstanceData;
	private final ApplicationNotificationHandler notificationHandler;

	private final Set<NotifiableAction> existingNotifiableActions = new HashSet<>();

	public NotificationUpdateControllerImpl(LifecycleEntityModel<ENTITY> lifecycleEntityModel, Function<ENTITY, OrganizationUnitView> unitByEntityFunction, ApplicationInstanceData applicationInstanceData) {
		this.lifecycleEntityModel = lifecycleEntityModel;
		this.unitByEntityFunction = unitByEntityFunction;
		this.applicationInstanceData = applicationInstanceData;
		this.notificationHandler = applicationInstanceData.getNotificationHandler();
	}

	private void handleEntityUpdate(ENTITY entity, NotifiableAction action, Function<ENTITY, Integer> detailIdFunction) {
		OrganizationUnitView unit = unitByEntityFunction.apply(entity);
		int detailId = detailIdFunction == null ? 0 : detailIdFunction.apply(entity);
		this.notificationHandler.sendEntityNotification(entity, unit, action.getActionId(), detailId, applicationInstanceData);
	}


	private void registerNotification(NotifiableAction notifiableAction, Function<ENTITY, Integer> detailIdFunction) {
		if (existingNotifiableActions.contains(notifiableAction)) {
			return;
		}
		switch (notifiableAction) {
			case NEW_ENTITY -> lifecycleEntityModel.addCreationListener(entity -> handleEntityUpdate(entity, notifiableAction, detailIdFunction));
			case UPDATED_ENTITY -> lifecycleEntityModel.addUpdateListener(entity -> handleEntityUpdate(entity, notifiableAction, detailIdFunction));
			case DELETED_ENTITY -> lifecycleEntityModel.addDeletionListener(entity -> handleEntityUpdate(entity, notifiableAction, detailIdFunction));
			case RESTORED_ENTITY -> lifecycleEntityModel.addRestoreListener(entity -> handleEntityUpdate(entity, notifiableAction, detailIdFunction));
		}
		existingNotifiableActions.add(notifiableAction);
	}


	@Override
	public void registerNotifyOnCreation() {
		registerNotification(NotifiableAction.NEW_ENTITY, null);
	}

	@Override
	public void registerNotifyOnCreation(Function<ENTITY, Integer> detailIdFunction) {
		registerNotification(NotifiableAction.NEW_ENTITY, detailIdFunction);
	}

	@Override
	public void registerNotifyOnUpdate() {
		registerNotification(NotifiableAction.UPDATED_ENTITY, null);
	}

	@Override
	public void registerNotifyOnUpdate(Function<ENTITY, Integer> detailIdFunction) {
		registerNotification(NotifiableAction.UPDATED_ENTITY, detailIdFunction);
	}

	@Override
	public void registerNotifyOnDeletion() {
		registerNotification(NotifiableAction.DELETED_ENTITY, null);
	}

	@Override
	public void registerNotifyOnDeletion(Function<ENTITY, Integer> detailIdFunction) {
		registerNotification(NotifiableAction.DELETED_ENTITY, detailIdFunction);
	}

	@Override
	public void registerNotifyOnRestore() {
		registerNotification(NotifiableAction.RESTORED_ENTITY, null);
	}

	@Override
	public void registerNotifyOnRestore(Function<ENTITY, Integer> detailIdFunction) {
		registerNotification(NotifiableAction.RESTORED_ENTITY, detailIdFunction);
	}

	@Override
	public void registerNotifyOnOrgMove() {
		registerNotifyOnOrgMove(null);
	}

	@Override
	public void registerNotifyOnOrgMove(Function<ENTITY, Integer> detailIdFunction) {
		lifecycleEntityModel.addBeforeUpdateAndAfterUpdateHandler((entity, entity2) -> {
			OrganizationUnitView newUnit = this.unitByEntityFunction.apply(entity);
			OrganizationUnitView originalUnit = this.unitByEntityFunction.apply(entity2);
			if (newUnit != null && originalUnit != null && !newUnit.equals(originalUnit)) {
				return () -> {
					int detailId = detailIdFunction  == null ? 0 : detailIdFunction.apply(entity);
					notificationHandler.sendEntityNotification(entity, originalUnit, newUnit, NotifiableAction.MOVE_TO_ORG_UNIT.getActionId(), detailId, applicationInstanceData);
				};
			} else {
				return () -> {};
			}
		});
	}

	@Override
	public void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction) {
		registerNotifyOnStatusChange(compareUpdateWithOriginalFunction, null);
	}

	@Override
	public void registerNotifyOnStatusChange(BiFunction<ENTITY, ENTITY, Boolean> compareUpdateWithOriginalFunction, Function<ENTITY, Integer> detailIdFunction) {
		lifecycleEntityModel.addBeforeUpdateAndAfterUpdateHandler((entity, entity2) -> {
			if (compareUpdateWithOriginalFunction.apply(entity, entity)) {
				return () -> {
					OrganizationUnitView unit = unitByEntityFunction.apply(entity);
					int detailId = detailIdFunction  == null ? 0 : detailIdFunction.apply(entity);
					notificationHandler.sendEntityNotification(entity, unit, NotifiableAction.ENTITY_STATUS_CHANGED.getActionId(), detailId, applicationInstanceData);
				};
			}
			return null;
		});
	}
}
