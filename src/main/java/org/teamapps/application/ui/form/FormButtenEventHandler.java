package org.teamapps.application.ui.form;

public interface FormButtenEventHandler<ENTITY> {
	void handleButtonClick(ENTITY entity, ENTITY synchronizedEntityCopy, FormEntityState entityState);

}
