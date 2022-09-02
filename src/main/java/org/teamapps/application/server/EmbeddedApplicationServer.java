package org.teamapps.application.server;

import org.teamapps.application.api.application.BaseApplicationBuilder;
import org.teamapps.config.TeamAppsConfiguration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class EmbeddedApplicationServer {

	public static void startServerWithApplication(String login, String password, boolean darkTheme, BaseApplicationBuilder applicationBuilder) throws Exception {
		startServerWithApplication(null, login, password, darkTheme, applicationBuilder);
	}

	public static void startServerWithApplication(InputStream baseDataInputStream, String login, String password, boolean darkTheme, BaseApplicationBuilder applicationBuilder) throws Exception {
		File basePath = new File("./server-data");
		startServerWithApplication(baseDataInputStream, login, password, darkTheme, applicationBuilder, basePath, 8080);
	}

	public static void startServerWithApplication(InputStream baseDataInputStream, String login, String password, boolean darkTheme, BaseApplicationBuilder applicationBuilder, File basePath, int port) throws Exception {
		startServerWithApplications(baseDataInputStream, login, password, darkTheme, basePath, port, applicationBuilder);
	}

	public static void startServerWithApplications(InputStream baseDataInputStream, String login, String password, boolean darkTheme, File basePath, int port, BaseApplicationBuilder... applicationBuilders) throws Exception {
		startServerWithApplications(baseDataInputStream, login, password, darkTheme, basePath, port, new TeamAppsConfiguration(),applicationBuilders);
	}

	public static void startServerWithApplications(InputStream baseDataInputStream, String login, String password, boolean darkTheme, File basePath, int port, TeamAppsConfiguration teamAppsConfiguration, BaseApplicationBuilder... applicationBuilders) throws Exception {
		basePath.mkdir();
		ApplicationServer applicationServer = new ApplicationServer(basePath, teamAppsConfiguration, port);
		URL resource = EmbeddedApplicationServer.class.getResource("/org/teamapps/application/api/emdedded/embedded-system.jar");
		SessionHandler sessionHandler = applicationServer.updateSessionHandler(resource);
		applicationServer.start();
		ApplicationLoader applicationLoader = (ApplicationLoader) sessionHandler;
		if (baseDataInputStream != null) {
			applicationLoader.createInitialData(baseDataInputStream);
		}
		if (login != null) {
			applicationLoader.createInitialLogin(login, password, darkTheme);
		}
		for (BaseApplicationBuilder builder : applicationBuilders) {
			applicationLoader.installAndLoadApplication(builder);
		}
	}
}
