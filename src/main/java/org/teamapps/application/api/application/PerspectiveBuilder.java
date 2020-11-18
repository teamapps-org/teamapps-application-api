package org.teamapps.application.api.application;

import org.teamapps.databinding.MutableValue;
import org.teamapps.icons.Icon;

public interface PerspectiveBuilder {

	Icon getIcon();

	String getName();

	String getTitleKey();

	String getDescriptionKey();

	boolean isPerspectiveAccessible(ApplicationInstanceData applicationInstanceData);

	ApplicationPerspective build(ApplicationInstanceData applicationInstanceData, MutableValue<String> perspectiveInfoBadgeValue);

}
