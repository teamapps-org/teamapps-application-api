package org.teamapps.application.ui.form;

public enum FormEntityState {
	NOTHING,
	NEW_UNCHANGED,
	NEW_MODIFIED,
	STORED_UNCHANGED,
	STORED_MODIFIED,
	DELETED,
	;

	public boolean isModified() {
		return this == NEW_MODIFIED || this == STORED_MODIFIED;
	}
}
