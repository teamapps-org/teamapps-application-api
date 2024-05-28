package org.teamapps.application.ux.window;

import org.teamapps.icons.Icon;

public class DialogeUtils {

	public static void showQuestion(Icon icon, String title, String text, Icon okButtonIcon, String okButtonCaption, Icon cancelButtonIcon, String cancelButtonCaption, Runnable onConfirmation) {
		BaseDialogue dialogue = new BaseDialogue(okButtonIcon, okButtonCaption, cancelButtonIcon, cancelButtonCaption);
		dialogue.setValues(icon, title, text);
		dialogue.setIcon(icon);
		dialogue.setTitle(title);
		dialogue.setModal(true);
		dialogue.onResult.addListener(result -> {
			if (result) {
				onConfirmation.run();
			}
		});
		dialogue.show(250);
	}

}
