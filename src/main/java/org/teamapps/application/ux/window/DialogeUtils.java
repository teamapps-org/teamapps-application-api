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
