/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2025 TeamApps.org
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
package org.teamapps.application.ui.changehistory;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.localization.Language;
import org.teamapps.application.ux.localize.FlagMap;
import org.teamapps.icon.flags.FlagIcon;
import org.teamapps.universaldb.index.translation.TranslatableText;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TranslatableFieldUtils {

	public static void updateFieldMessage(AbstractField<?> field, TranslatableText value, ApplicationInstanceData applicationInstanceData) {
		String msg;
		if (value == null) {
			String language = applicationInstanceData.getUser().getLanguage();
			msg = applicationInstanceData.getLocalized("translatableTextField.emptyField.desc", getLanguageWithIcon(language, true, applicationInstanceData));
		} else {

			String originalLanguage = value.getOriginalLanguage();
			msg = applicationInstanceData.getLocalized("translatableTextField.originalLanguage") + ": " + getLanguageWithIcon(value.getOriginalLanguage(), true, applicationInstanceData) + "<br>";
			msg += applicationInstanceData.getLocalized("translatableTextField.originalText") + ": " + value.getText() + "<br>";
			msg += applicationInstanceData.getLocalized("translatableTextField.translations") + ":<br>";


			for (Map.Entry<String, String> entry : value.getTranslationMap().entrySet().stream().filter(entry -> !entry.getKey().equals(originalLanguage)).sorted(Map.Entry.comparingByKey()).toList()) {
				msg += getLanguageWithIcon(entry.getKey(), false, applicationInstanceData) + ": " + entry.getValue() + "<br>";
			}
		}
		List<FieldMessage> fieldMessages = Collections.singletonList(new FieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS, FieldMessage.Severity.INFO, msg));
		field.setCustomFieldMessages(fieldMessages);
	}


	public static String getLanguage(String isoCode, ApplicationInstanceData applicationInstanceData) {
		Language languageByIsoCode = Language.getLanguageByIsoCode(isoCode);
		return languageByIsoCode != null ? languageByIsoCode.getLanguageLocalized(applicationInstanceData) : isoCode;
	}

	public static String getUnicodeFlag(String isoCode) {
		FlagIcon flagIcon = Language.getLanguageIconByIsoCode(isoCode);
		if (flagIcon != null && flagIcon.getCountryCode() != null) {
			String unicodeFlag = FlagMap.getFlagByCountryCode(flagIcon.getCountryCode().toUpperCase());
			return unicodeFlag != null ? unicodeFlag : "";
		} else {
			return "";
		}
	}

	public static String getLanguageWithIcon(String isoCode, boolean bold, ApplicationInstanceData applicationInstanceData) {
		return bold ? getUnicodeFlag(isoCode) + " <b>" + getLanguage(isoCode, applicationInstanceData) + "</b>" : getUnicodeFlag(isoCode) + " " + getLanguage(isoCode, applicationInstanceData);
	}
}
