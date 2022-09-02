package org.teamapps.application.server;

import org.teamapps.application.api.application.BaseApplicationBuilder;

import java.io.InputStream;

public interface ApplicationLoader {

	boolean installAndLoadApplication(BaseApplicationBuilder baseApplicationBuilder);

	void createInitialLogin(String login, String password, boolean darkTheme);

	void createInitialData(InputStream inputStream);

}
