package org.teamapps.application.ui.model;

import org.teamapps.application.api.application.entity.EntityUpdateType;
import org.teamapps.event.Event;
import org.teamapps.universaldb.record.EntityBuilder;

public interface UpdateListenerEntityModel<ENTITY> {

	EntityBuilder<ENTITY> getEntityBuilder();

	Event<EntityUpdateType> getOnSelectedEntityExternallyChanged();

	Event<EntityUpdateType> getOnEntityExternallyChanged();


}
