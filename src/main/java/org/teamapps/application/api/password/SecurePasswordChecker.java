/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2022 TeamApps.org
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
