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
import org.teamapps.application.api.localization.ApplicationLocalizationProvider;
import org.teamapps.application.api.notification.ApplicationNotificationHandler;
import org.teamapps.application.api.organization.UserRoleType;
import org.teamapps.application.api.privilege.ApplicationPrivilegeProvider;
import org.teamapps.application.api.search.UserSearch;
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
import org.teamapps.universaldb.message.MessageStore;
import org.teamapps.universaldb.record.EntityBuilder;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.session.SessionContext;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ApplicationInstanceData extends ApplicationPrivilegeProvider, ApplicationLocalizationProvider {

	SessionUser getUser();

	OrganizationFieldView getOrganizationField();

	int getManagedApplicationId();

	DocumentConverter getDocumentConverter();

	MultiProgressDisplay getMultiProgressDisplay();

	default LocalizedFormatter getLocalizedFormatter() {
		return getUser().getLocalizedFormatter();
	}

	default void runTaskAsync(Icon icon, String title, Runnable task) {
		MultiProgressDisplay multiProgressDisplay = getMultiProgressDisplay();
		multiProgressDisplay.addTask(icon, title, progressMonitor -> {
			task.run();
			progressMonitor.markCompleted();
		});
	}

	default <RESULT> void runTaskAsync(Icon icon, String title, Supplier<RESULT> task, Consumer<RESULT> uiResultTask) {
		SessionContext context = SessionContext.current();
		MultiProgressDisplay multiProgressDisplay = getMultiProgressDisplay();
		multiProgressDisplay.addTask(icon, title, progressMonitor -> {
			RESULT result = task.get();
			progressMonitor.markCompleted();
			if (uiResultTask != null) {
				context.runWithContext(() -> uiResultTask.accept(result));
			}
		});
	}

	File createTempFile();

	File createTempFile(String prefix, String suffix);

	void showPerspective(Perspective perspective);

	ApplicationPerspective showApplicationPerspective(String perspectiveName);

	ApplicationDesktop createApplicationDesktop();

	UiComponentFactory getComponentFactory();

	boolean isDarkTheme();

	ApplicationConfig<?> getApplicationConfig();

	void writeActivityLog(Level level, String title, String data);

	void writeExceptionLog(Level level, String title, Throwable throwable);

	Integer getOrganizationUserWithRole(OrganizationUnitView orgUnit, UserRoleType userRoleType);

	String getOrganizationUserNameWithRole(OrganizationUnitView orgUnit, UserRoleType userRoleType, boolean lastNameFirst);

	List<Integer> getOrganizationUsersWithRole(OrganizationUnitView orgUnit, UserRoleType userRoleType);

	<ENTITY> void registerEntityUpdateListener(EntityBuilder<ENTITY> entityBuilder, Consumer<EntityUpdate<ENTITY>> listener);

	<TYPE> Event<TYPE> getUserSessionEvent(String name);

	<TYPE>TwoWayBindableValue<TYPE> getBindableValue(String name);

	<TYPE>TwoWayBindableValue<TYPE> getBindableValue(String name, boolean fireAlways);

	ReplicatedStateMachine getReplicatedStateMachine(String name);

	String createPublicLinkForResource(Resource resource, Duration availabilityDuration);

	<MESSAGE extends Message> MessageStore<MESSAGE> getMessageStore(String name);

	UserSearch createUserSearch(String authCode);

	ApplicationNotificationHandler getNotificationHandler();
}
