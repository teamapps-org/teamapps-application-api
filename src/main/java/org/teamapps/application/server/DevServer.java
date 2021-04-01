/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2021 TeamApps.org
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
package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationPerspectiveBuilder;

import org.teamapps.model.controlcenter.OrganizationUnitView;
import org.teamapps.reporting.convert.DocumentConverter;
import org.teamapps.server.undertow.embedded.TeamAppsUndertowEmbeddedServer;
import org.teamapps.universaldb.UniversalDB;
import org.teamapps.universaldb.schema.SchemaInfoProvider;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class DevServer {

	private final ApplicationPerspectiveBuilder applicationBuilder;
	private int port = 8080;
	private File path = new File("./dev-database");
	private List<OrganizationUnitView> OrganizationUnitViews = Collections.emptyList();
	private DocumentConverter documentConverter;

	public static DevServer create(ApplicationPerspectiveBuilder applicationBuilder) {
		return new DevServer(applicationBuilder);
	}

	protected DevServer(ApplicationPerspectiveBuilder applicationBuilder) {
		this.applicationBuilder = applicationBuilder;
	}

	public DevServer withDbPath(File path) {
		this.path = path;
		return this;
	}

	public DevServer withPort(int port) {
		this.port = port;
		return this;
	}

	public DevServer withOrganizationUnitViews(List<OrganizationUnitView> OrganizationUnitViews) {
		this.OrganizationUnitViews = OrganizationUnitViews;
		return this;
	}

	public DevServer withDocumentConverter(DocumentConverter documentConverter) {
		this.documentConverter = documentConverter;
		return this;
	}

	public void start() {
		try {
			path.mkdir();
			SchemaInfoProvider databaseModel = applicationBuilder.getDatabaseModel();
			if (databaseModel != null) {
				UniversalDB.createStandalone(path, databaseModel);
			}
			WebController webController = sessionContext -> {
				SessionContext context = SessionContext.current();
				RootPanel rootPanel = context.addRootPanel();
				rootPanel.setContent(new DevApplication(applicationBuilder, OrganizationUnitViews, documentConverter).getComponent());
				String defaultBackground = "/resources/backgrounds/default-bl.jpg";
				context.registerBackgroundImage("default", defaultBackground, defaultBackground);
				context.setBackgroundImage("default", 0);
			};
			File webAppDirectory = Files.createTempDirectory("teamapps").toRealPath().toFile();
			TeamAppsUndertowEmbeddedServer server = new TeamAppsUndertowEmbeddedServer(webController, webAppDirectory, port);
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
