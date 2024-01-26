package org.teamapps.application.api.search;

import java.util.List;

public interface UserSearch {

	UserMatch getUser(int userId);

	String getUserTasks(int userId);

	List<UserMatch> search(UserSearchBuilder searchBuilder, int minScore);


}
