package org.teamapps.application.ux.form;

public interface FormControllerEventHandler<ENTITY> {

	boolean handleNewEntityRequest();
	boolean handleSaveRequest(ENTITY entity);
	boolean handleRevertChangesRequest(ENTITY entity);
	boolean handleDeleteRequest(ENTITY entity);
	boolean handleRestoreDeletedRecordRequest(ENTITY entity);

}
