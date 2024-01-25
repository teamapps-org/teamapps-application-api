package org.teamapps.application.api.search;

import org.teamapps.application.api.application.ApplicationInstanceData;

import java.util.List;

public interface UserSearch {

	void setAuthCode(String authCode, ApplicationInstanceData applicationInstanceData);

	UserMatch getUser(int userId);

	List<UserMatch> searchByName(String first, String last);

	List<UserMatch> searchByEmail(String email);

	List<UserMatch> searchByPhone(String phone);

	List<UserMatch> searchByAddress(String street, String postalCode, String city, String countryIso);

	List<UserMatch> extendedSearch(UserMatch template);


}
