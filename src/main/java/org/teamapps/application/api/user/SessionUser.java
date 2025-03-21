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
package org.teamapps.application.api.user;

import com.ibm.icu.util.ULocale;
import org.teamapps.event.Event;
import org.teamapps.universaldb.context.UserContext;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Locale;

public interface SessionUser extends UserContext {

	int getId();

	String getFirstName();

	String getFirstNameTranslated();

	String getLastName();

	String getLastNameTranslated();

	String getName(boolean lastNameFirst);

	String getDescription();

	String getCountry();

	String getProfilePictureLink();

	String getLargeProfilePictureLink();

	SessionContext getSessionContext();

	ULocale getULocale();

	Locale getLocale();

	List<String> getRankedLanguages();

	boolean isDarkTheme();

	LocalizedFormatter getLocalizedFormatter();

	Event<Void> onUserLogout();

	boolean isAppLogin();

	UserAppConferenceHandler getUserAppConferenceHandler();

}
