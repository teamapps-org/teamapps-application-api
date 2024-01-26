package org.teamapps.application.api.search;

public record UserMatch(int userId, UserGender gender, String firstName, String lastName, String street, String postalCode, String city, String countryIso, String phone, String email, int matchScore) {

	public enum UserGender {
		MALE,
		FEMALE
	}
}
