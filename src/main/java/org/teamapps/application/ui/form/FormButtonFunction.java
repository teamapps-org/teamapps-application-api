package org.teamapps.application.ui.form;

import org.teamapps.application.ui.privilege.EntityPrivileges;

public interface FormButtonFunction<ENTITY> {

	boolean isAllowed(ENTITY entity, ENTITY synchronizedEntityCopy, EntityPrivileges<ENTITY> entityPrivileges);
}
