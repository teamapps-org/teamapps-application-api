package org.teamapps.application.api.password;

import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.*;
import me.gosimple.nbvcxz.scoring.Result;
import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.user.SessionUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SecurePasswordChecker {

	public static List<String> checkPasswordWithLocalizedWarnings(String password, ApplicationInstanceData applicationInstanceData) {
		SessionUser user = applicationInstanceData.getUser();
		List<String> warnings = checkPassword(password, user.getFirstName(), user.getLastName());
		if (warnings == null) {
			return null;
		} else {
			return warnings.stream().map(applicationInstanceData::getLocalized).collect(Collectors.toList());
		}
	}

	public static List<String> checkPassword(String password, String... excluded) {
		DictionaryBuilder dictionaryBuilder = new DictionaryBuilder()
				.setDictionaryName("exclude")
				.setExclusion(true);

		Arrays.stream(excluded).forEach(ex -> dictionaryBuilder.addWord(ex, 0));
		Dictionary dictionary = dictionaryBuilder.createDictionary();

		List<Dictionary> dictionaryList = ConfigurationBuilder.getDefaultDictionaries();
		dictionaryList.add(dictionary);

		Configuration configuration = new ConfigurationBuilder()
				.setMinimumEntropy(40d)
				.setLocale(Locale.ENGLISH)
				.setDictionaries(dictionaryList)
				.createConfiguration();

		Nbvcxz passwordChecker = new Nbvcxz(configuration);
		Result result = passwordChecker.estimate(password);
		if (result.isMinimumEntropyMet()) {
			return null;
		} else {
			List<String> warningKeys = new ArrayList<>();
			Feedback feedback = result.getFeedback();
			if (feedback != null) {
				if (feedback.getWarningKey() != null) {
					warningKeys.add(feedback.getWarningKey());
				}
				if (feedback.getSuggestionKeys() != null) {
					warningKeys.addAll(feedback.getSuggestionKeys());
				}
			}
			if (warningKeys.isEmpty()) {
				warningKeys.add("feedback.extra.suggestions.addAnotherWord");
			}
			return warningKeys.stream().map(s -> s.replace("feedback.", "userSettings.passwordCheck.")).collect(Collectors.toList());
		}
	}



}
