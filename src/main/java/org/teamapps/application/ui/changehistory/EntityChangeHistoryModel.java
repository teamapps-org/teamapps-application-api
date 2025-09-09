package org.teamapps.application.ui.changehistory;

import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.universaldb.pojo.AbstractUdbEntity;
import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.universaldb.record.EntityBuilder;

public interface EntityChangeHistoryModel<ENTITY> {

	EntityBuilder<ENTITY> getEntityBuilder();

	ObservableValue<ENTITY> getSelectedEntity();

	static <ENTITY extends Entity<ENTITY>> EntityChangeHistoryModel<ENTITY> createModelFromEntity(ENTITY entity) {
		return new EntityChangeHistoryModel<ENTITY>() {
			@Override
			public EntityBuilder<ENTITY> getEntityBuilder() {
				return (AbstractUdbEntity<ENTITY>) entity;
			}

			@Override
			public ObservableValue<ENTITY> getSelectedEntity() {
				return TwoWayBindableValue.create(entity);
			}
		};
	}


}
