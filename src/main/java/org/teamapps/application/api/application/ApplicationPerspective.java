package org.teamapps.application.api.application;

import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.component.Component;

public interface ApplicationPerspective {

	Component getPerspectiveMenuPanel();

	Perspective getPerspective();
}
