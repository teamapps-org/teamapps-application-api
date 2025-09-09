package org.teamapps.application.ui.notification;

import org.teamapps.application.api.notification.NotifiableAction;
import org.teamapps.universaldb.pojo.Entity;

import java.util.function.Function;

public class NotificationUpdateData<ENTITY extends Entity<ENTITY>> {

	private final NotifiableAction  notifiableAction;
	private final Function<ENTITY, Integer> detailIdFunction;

	public NotificationUpdateData(NotifiableAction notifiableAction) {
		this.notifiableAction = notifiableAction;
		this.detailIdFunction = null;
	}

	public NotificationUpdateData(NotifiableAction notifiableAction, Function<ENTITY, Integer> detailIdFunction) {
		this.notifiableAction = notifiableAction;
		this.detailIdFunction = detailIdFunction;
	}

	public int getDetailId(ENTITY entity) {
		return detailIdFunction == null ? 0 : detailIdFunction.apply(entity);
	}

	public NotifiableAction getNotifiableAction() {
		return notifiableAction;
	}

	public Function<ENTITY, Integer> getDetailIdFunction() {
		return detailIdFunction;
	}
}
