package org.teamapps.application.server;

import org.teamapps.application.api.application.ApplicationPerspectiveBuilder;
import org.teamapps.application.api.organization.OrgUnit;
import org.teamapps.server.undertow.embedded.TeamAppsUndertowEmbeddedServer;
import org.teamapps.universaldb.UniversalDB;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class DevServer {

	private final ApplicationPerspectiveBuilder applicationBuilder;
	private int port = 8080;
	private File path = new File("./dev-database");
	private List<OrgUnit> orgUnits = Collections.emptyList();

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

	public DevServer withOrgUnits(List<OrgUnit> orgUnits) {
		this.orgUnits = orgUnits;
		return this;
	}

	public void start() {
		try {
			path.mkdir();
			UniversalDB.createStandalone(path, applicationBuilder.getDatabaseModel());
			WebController webController = sessionContext -> {
				SessionContext context = SessionContext.current();
				RootPanel rootPanel = context.addRootPanel();
				rootPanel.setContent(new DevApplication(applicationBuilder, orgUnits).getComponent());
				String defaultBackground = "/resources/backgrounds/default-bl.jpg";
				context.registerBackgroundImage("default", defaultBackground, defaultBackground);
				context.setBackgroundImage("default", 0);
			};
			TeamAppsUndertowEmbeddedServer server = new TeamAppsUndertowEmbeddedServer(webController, port);
			server.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
