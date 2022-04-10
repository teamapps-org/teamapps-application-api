package org.teamapps.application.api.application.entity;

public class EntityUpdate<ENTITY> {

	private final ENTITY entity;
	private final int userId;
	private final EntityUpdateType updateType;

	public EntityUpdate(ENTITY entity, int userId, EntityUpdateType updateType) {
		this.entity = entity;
		this.userId = userId;
		this.updateType = updateType;
	}

	public ENTITY getEntity() {
		return entity;
	}

	public int getUserId() {
		return userId;
	}

	public EntityUpdateType getUpdateType() {
		return updateType;
	}
}
