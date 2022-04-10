package org.teamapps.application.api.application.entity;

import org.teamapps.universaldb.update.RecordUpdateType;

public enum EntityUpdateType {
	CREATE,
	UPDATE,
	DELETE,
	RESTORE,
	;

	public static EntityUpdateType create(RecordUpdateType recordUpdateType) {
		return switch (recordUpdateType) {
			case CREATE -> CREATE;
			case UPDATE -> UPDATE;
			case DELETE -> DELETE;
			case RESTORE -> RESTORE;
		};
	}

}
