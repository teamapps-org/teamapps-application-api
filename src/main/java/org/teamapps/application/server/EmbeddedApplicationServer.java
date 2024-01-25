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
package org.teamapps.application.server;

import org.teamapps.application.api.application.BaseApplicationBuilder;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.model.controlcenter.OrganizationUnitView;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class EmbeddedApplicationServer {

	private TeamAppsConfiguration teamAppsConfiguration = new TeamAppsConfiguration();
	private int port = 8080;
	private InputStream initialData;
	private BaseApplicationBuilder[] applicationBuilders;

	private String login = "admin";
	private String password = "admin";
	private String language = "en";
	private boolean darkTheme = false;
	private File basePath = new File("./server-data");

	private String proxyHost = null;
	private int proxyPort = 0;

	public static EmbeddedApplicationServer build() {
		return new EmbeddedApplicationServer();
	}

	public EmbeddedApplicationServer setTeamAppsConfiguration(TeamAppsConfiguration teamAppsConfiguration) {
		this.teamAppsConfiguration = teamAppsConfiguration;
		return this;
	}

	public EmbeddedApplicationServer setPort(int port) {
		this.port = port;
		return this;
	}

	public EmbeddedApplicationServer setInitialData(InputStream initialData) {
		this.initialData = initialData;
		return this;
	}

	public EmbeddedApplicationServer setApplicationBuilders(BaseApplicationBuilder... applicationBuilders) {
		this.applicationBuilders = applicationBuilders;
		return this;
	}

	public EmbeddedApplicationServer setLogin(String login) {
		this.login = login;
		return this;
	}

	public EmbeddedApplicationServer setPassword(String password) {
		this.password = password;
		return this;
	}

	public EmbeddedApplicationServer setLanguage(String language) {
		this.language = language;
		return this;
	}

	public EmbeddedApplicationServer setDarkTheme(boolean darkTheme) {
		this.darkTheme = darkTheme;
		return this;
	}

	public EmbeddedApplicationServer setBasePath(File basePath) {
		this.basePath = basePath;
		return this;
	}

	public EmbeddedApplicationServer setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	public EmbeddedApplicationServer setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}

	public void startServer() {
		try {
			if (basePath.getParentFile().exists()) {
				basePath.mkdir();
			}
			System.out.println("Starting server with port:" + port + ", data path:" + basePath.getAbsolutePath());
			ApplicationServer applicationServer = new ApplicationServer(basePath, teamAppsConfiguration, port);
			InputStream inputStream = EmbeddedApplicationServer.class.getResourceAsStream("/org/teamapps/application/api/embedded/embedded-system.jar");
			Path tempFile = Files.createTempFile("temp", ".jar");
			Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
			SessionHandler sessionHandler = applicationServer.updateSessionHandler(tempFile.toUri().toURL());
			applicationServer.start();
			ApplicationLoader applicationLoader = (ApplicationLoader) sessionHandler;
			if (initialData != null) {
				applicationLoader.createInitialData(initialData);
			}
			if (login != null) {
				applicationLoader.createInitialLogin(login, password, language, darkTheme);
			}
			if (proxyHost != null) {
				applicationLoader.setProxy(proxyHost, proxyPort);
			}
			if (applicationBuilders != null) {
				for (BaseApplicationBuilder builder : applicationBuilders) {
					applicationLoader.installAndLoadApplication(builder);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
