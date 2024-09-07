package org.teamapps.application.api.user;

public interface UserAppConferenceHandler {

	void openConference(String conferenceUuid, String participantRole);

	void closeConferenceWindow();

}
