package org.teamapps.application.api.application;

import org.teamapps.application.api.config.ApplicationConfig;
import org.teamapps.application.api.desktop.ApplicationDesktop;
import org.teamapps.application.api.privilege.*;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.icons.Icon;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ApplicationInstanceDataMethods {

	ApplicationInstanceData getApplicationInstanceData();
	
	default SessionContext getContext() {
		return getApplicationInstanceData().getUser().getSessionContext();
	}

	default void writeActivityLog(String title, String data) {
		getApplicationInstanceData().writeActivityLog(title, data);
	}

	default void writeExceptionLog(String title, Throwable throwable) {
		getApplicationInstanceData().writeExceptionLog(title, throwable);
	}

	default SessionUser getUser() {
		return getApplicationInstanceData().getUser();
	}

	default MultiProgressDisplay getMultiProgressDisplay() {
		return getApplicationInstanceData().getMultiProgressDisplay();
	}

	default <RESULT> void runTaskAsync(Icon icon, String title, Supplier<RESULT> task, Consumer<RESULT> uiResultTask) {
		getApplicationInstanceData().runTaskAsync(icon, title, task, uiResultTask);
	}

	default void showPerspective(Perspective perspective) {
		getApplicationInstanceData().showPerspective(perspective);
	}

	default ApplicationDesktop createApplicationDesktop() {
		return getApplicationInstanceData().createApplicationDesktop();
	}

	default ApplicationConfig<?> getApplicationConfig() {
		return getApplicationInstanceData().getApplicationConfig();
	}

	default String getLocalized(String key, Object... parameters) {
		return getApplicationInstanceData().getLocalized(key, parameters);
	}

	default String getLocalized(TranslatableText translatableText) {
		return getApplicationInstanceData().getLocalized(translatableText);
	}

	default boolean isAllowed(SimplePrivilege simplePrivilege) {
		return getApplicationInstanceData().isAllowed(simplePrivilege);
	}

	default boolean isAllowed(SimpleOrganizationalPrivilege group, OrganizationUnitView OrganizationUnitView) {
		return getApplicationInstanceData().isAllowed(group, OrganizationUnitView);
	}

	default boolean isAllowed(SimpleCustomObjectPrivilege group, PrivilegeObject privilegeObject) {
		return getApplicationInstanceData().isAllowed(group, privilegeObject);
	}

	default boolean isAllowed(StandardPrivilegeGroup group, Privilege privilege) {
		return getApplicationInstanceData().isAllowed(group, privilege);
	}

	default boolean isAllowed(OrganizationalPrivilegeGroup group, Privilege privilege, OrganizationUnitView OrganizationUnitView) {
		return getApplicationInstanceData().isAllowed(group, privilege, OrganizationUnitView);
	}

	default boolean isAllowed(CustomObjectPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
		return getApplicationInstanceData().isAllowed(group, privilege, privilegeObject);
	}

	default List<OrganizationUnitView> getAllowedUnits(SimpleOrganizationalPrivilege simplePrivilege) {
		return getApplicationInstanceData().getAllowedUnits(simplePrivilege);
	}

	default List<OrganizationUnitView> getAllowedUnits(OrganizationalPrivilegeGroup group, Privilege privilege) {
		return getApplicationInstanceData().getAllowedUnits(group, privilege);
	}

	default List<PrivilegeObject> getAllowedPrivilegeObjects(SimpleCustomObjectPrivilege simplePrivilege) {
		return getApplicationInstanceData().getAllowedPrivilegeObjects(simplePrivilege);
	}

	default List<PrivilegeObject> getAllowedPrivilegeObjects(CustomObjectPrivilegeGroup group, Privilege privilege) {
		return getApplicationInstanceData().getAllowedPrivilegeObjects(group, privilege);
	}
}
