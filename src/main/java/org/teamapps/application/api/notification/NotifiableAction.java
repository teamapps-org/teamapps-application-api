package org.teamapps.application.api.notification;

public enum NotifiableAction {

	NEW_ENTITY(1),
	UPDATED_ENTITY(2),
	DELETED_ENTITY(3),
	RESTORED_ENTITY(4),
	MOVE_TO_ORG_UNIT(5),
	ENTITY_STATUS_CHANGED(6),

	;
	private final int actionId;

	NotifiableAction(int actionId) {
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public static NotifiableAction fromActionId(int actionId) {
		for (NotifiableAction action : values()) {
			if (action.getActionId() == actionId) {
				return action;
			}
		}
		return null;
	}
}
