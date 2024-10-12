/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.application.api.application;

import org.slf4j.event.Level;
import org.teamapps.application.api.application.entity.EntityUpdate;
import org.teamapps.application.api.application.perspective.ApplicationPerspective;
import org.teamapps.application.api.config.ApplicationConfig;
import org.teamapps.application.api.desktop.ApplicationDesktop;
import org.teamapps.application.api.notification.ApplicationNotificationHandler;
import org.teamapps.application.api.privilege.*;
import org.teamapps.application.api.state.ReplicatedStateMachine;
import org.teamapps.application.api.ui.UiComponentFactory;
import org.teamapps.application.api.user.LocalizedFormatter;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.databinding.TwoWayBindableValue;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.message.protocol.message.Message;
import org.teamapps.model.controlcenter.OrganizationFieldView;
import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.reporting.convert.DocumentConverter;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.universaldb.message.MessageStore;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.session.SessionContext;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ApplicationInstanceDataMethods {

	ApplicationInstanceData getApplicationInstanceData();

	default OrganizationFieldView getOrganizationFieldView() {
		return getApplicationInstanceData().getOrganizationField();
	}

	default int getManagedApplicationId() {
		return getApplicationInstanceData().getManagedApplicationId();
	}

	default DocumentConverter getDocumentConverter() {
		return getApplicationInstanceData().getDocumentConverter();
	}

	default UiComponentFactory getComponentFactory() {
		return getApplicationInstanceData().getComponentFactory();
	}

	default SessionContext getContext() {
		return getApplicationInstanceData().getUser().getSessionContext();
	}

	default void writeActivityLog(Level level, String title, String data) {
		getApplicationInstanceData().writeActivityLog(level, title, data);
	}

	default void writeExceptionLog(Level level, String title, Throwable throwable) {
		getApplicationInstanceData().writeExceptionLog(level, title, throwable);
	}

	default SessionUser getUser() {
		return getApplicationInstanceData().getUser();
	}

	default MultiProgressDisplay getMultiProgressDisplay() {
		return getApplicationInstanceData().getMultiProgressDisplay();
	}

	default LocalizedFormatter getLocalizedFormatter() {
		return getApplicationInstanceData().getLocalizedFormatter();
	}

	default void runTaskAsync(Icon icon, String title, Runnable task) {
		getApplicationInstanceData().runTaskAsync(icon, title, task);
	}

	default <RESULT> void runTaskAsync(Icon icon, String title, Supplier<RESULT> task, Consumer<RESULT> uiResultTask) {
		getApplicationInstanceData().runTaskAsync(icon, title, task, uiResultTask);
	}

	default void showPerspective(Perspective perspective) {
		getApplicationInstanceData().showPerspective(perspective);
	}

	default ApplicationPerspective showApplicationPerspective(String perspectiveName) {
		return getApplicationInstanceData().showApplicationPerspective(perspectiveName);
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

	default String getLocalized(String key, List<String> languagePriorityOrder, Object... parameters) {
		return getApplicationInstanceData().getLocalized(key, languagePriorityOrder, parameters);
	}


	default String getLocalized(String key, Locale locale, Object... parameters) {
		return getLocalized(key, Collections.singletonList(locale.getLanguage()), parameters);
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

	default boolean isAllowed(RoleAssignmentDelegatedCustomPrivilegeGroup group, Privilege privilege, PrivilegeObject privilegeObject) {
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

	default List<PrivilegeObject> getAllowedPrivilegeObjects(RoleAssignmentDelegatedCustomPrivilegeGroup group, Privilege privilege) {
		return getApplicationInstanceData().getAllowedPrivilegeObjects(group, privilege);
	}

	default <ENTITY> void registerEntityUpdateListener(EntityBuilder<ENTITY> entityBuilder, Consumer<EntityUpdate<ENTITY>> listener) {
		getApplicationInstanceData().registerEntityUpdateListener(entityBuilder, listener);
	}

	default String createPublicLinkForResource(Resource resource, Duration availabilityDuration) {
		return getApplicationInstanceData().createPublicLinkForResource(resource, availabilityDuration);
	}


	default <TYPE> Event<TYPE> getUserSessionEvent(String name) {
		return getApplicationInstanceData().getUserSessionEvent(name);
	}

	default <TYPE> TwoWayBindableValue<TYPE> getBindableValue(String name) {
		return getApplicationInstanceData().getBindableValue(name);
	}

	default ReplicatedStateMachine getReplicatedStateMachine(String name) {
		return getApplicationInstanceData().getReplicatedStateMachine(name);
	}

	default <MESSAGE extends Message> MessageStore<MESSAGE> getMessageStore(String name) {
		return getApplicationInstanceData().getMessageStore(name);
	}

	default ApplicationNotificationHandler getNotificationHandler() {
		return getApplicationInstanceData().getNotificationHandler();
	}
}
