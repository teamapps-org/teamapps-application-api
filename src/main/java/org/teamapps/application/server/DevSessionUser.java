package org.teamapps.application.server;

import com.ibm.icu.util.ULocale;
import org.teamapps.application.api.user.SessionUser;
import org.teamapps.ux.session.SessionContext;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DevSessionUser implements SessionUser {

	private final SessionContext context;
	private final Locale locale;

	public DevSessionUser(SessionContext context, Locale locale) {
		this.context = context;
		this.locale = locale;
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public String getName(boolean lastNameFirst) {
		return "John Doe";
	}

	@Override
	public String getProfilePictureLink() {
		return null;
	}

	@Override
	public String getLargeProfilePictureLink() {
		return null;
	}

	@Override
	public SessionContext getSessionContext() {
		return context;
	}

	@Override
	public ULocale getULocale() {
		return ULocale.forLocale(locale);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public List<String> getRankedLanguages() {
		return Collections.singletonList(locale.getLanguage());
	}
}
