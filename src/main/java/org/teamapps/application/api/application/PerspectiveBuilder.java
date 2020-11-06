package org.teamapps.application.api.application;

import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.api.Icon;

public interface PerspectiveBuilder {

	Icon getIcon();

	String getName();

	String getTitleKey();

	String getDescriptionKey();

	ApplicationPerspective build(ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue);

}
