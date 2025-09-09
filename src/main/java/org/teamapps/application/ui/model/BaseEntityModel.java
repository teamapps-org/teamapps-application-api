package org.teamapps.application.ui.model;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.databinding.ObservableValue;

import java.util.List;

public interface BaseEntityModel<ENTITY> {

	ObservableValue<List<ENTITY>> getEntities();

	ObservableValue<Integer> getEntityCount();

	ObservableValue<ENTITY> getSelectedEntity();

	ObservableValue<Boolean> getShowDeletedEntities();

	String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData);

	void handleShowDeletedEntities(boolean showDeletedEntities);

	void handleEntitySelection(ENTITY record);

	void handleFullTextQuery(String query);

}
