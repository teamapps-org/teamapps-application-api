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
