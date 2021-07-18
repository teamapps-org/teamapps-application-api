package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Dictionary;
import org.teamapps.application.api.theme.ApplicationIcons;
import org.teamapps.icons.composite.CompositeIcon;
import org.teamapps.ux.component.toolbar.ToolbarButton;

public class FormButtonUtils {

	public static ToolbarButton createNewButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(ApplicationIcons.ADD, applicationInstanceData.getLocalized(Dictionary.NEW));
	}

	public static ToolbarButton createAddButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(ApplicationIcons.ADD, applicationInstanceData.getLocalized(Dictionary.ADD));
	}

	public static ToolbarButton createSaveButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(ApplicationIcons.FLOPPY_DISK, applicationInstanceData.getLocalized(Dictionary.SAVE));
	}

	public static ToolbarButton createRevertButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(ApplicationIcons.UNDO, applicationInstanceData.getLocalized(Dictionary.REVERT_CHANGES));
	}

	public static ToolbarButton createDeleteButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(ApplicationIcons.GARBAGE_EMPTY, applicationInstanceData.getLocalized(Dictionary.DELETE));
	}

	public static ToolbarButton createRestoreButton(ApplicationInstanceData applicationInstanceData) {
		return ToolbarButton.createSmall(CompositeIcon.of(ApplicationIcons.GARBAGE_MAKE_EMPTY, ApplicationIcons.ADD), applicationInstanceData.getLocalized(Dictionary.RESTORE));
	}

}
