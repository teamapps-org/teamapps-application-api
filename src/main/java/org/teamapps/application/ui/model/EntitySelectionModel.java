package org.teamapps.application.ui.model;

public interface EntitySelectionModel<ENTITY> {

	void handleEntitySelection(ENTITY record);

	void handleSelectPreviousEntity();

	void handleSelectNextEntity();

	void handleSelectFirstEntity();

}
