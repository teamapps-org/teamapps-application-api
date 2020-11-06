package org.teamapps.application.api.organization;

import org.teamapps.universaldb.pojo.Identifiable;

import java.util.List;

public interface OrgUnit extends Identifiable {

	OrgUnit getParent();

	List<OrgUnit> getChildren();

	String getTitle();



}
