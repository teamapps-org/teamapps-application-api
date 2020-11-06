package org.teamapps.application.api.application;

import java.util.List;

public interface ApplicationPerspectiveBuilder extends ApplicationBuilder {

	List<PerspectiveBuilder> getPerspectiveBuilders();

	@Override
	default Application build(ApplicationInstanceData applicationInstanceData) {
		return null;
	}
}
