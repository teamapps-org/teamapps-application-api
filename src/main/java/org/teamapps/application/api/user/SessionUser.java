package org.teamapps.application.api.user;

import com.ibm.icu.util.ULocale;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Locale;

public interface SessionUser {

	int getId();

	String getName(boolean lastNameFirst);

	String getProfilePictureLink();

	String getLargeProfilePictureLink();

	SessionContext getSessionContext();

	ULocale getULocale();

	Locale getLocale();

	List<String> getRankedLanguages();


}
