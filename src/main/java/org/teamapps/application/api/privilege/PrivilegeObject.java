package org.teamapps.application.api.privilege;

import org.teamapps.icons.Icon;

import java.util.List;

public interface PrivilegeObject {

	int getId();

	Icon getIcon();

	String getTitleKey();

	String getDescriptionKey();

	PrivilegeObject getParent();

	List<PrivilegeObject> getChildren();
}
