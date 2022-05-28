package org.teamapps.application.api.state;

import org.teamapps.cluster.state.ReplicatedState;
import org.teamapps.cluster.state.ReplicatedStateRegistry;

public class StateRegistry implements ReplicatedStateRegistry {


	@Override
	public String getLocalNodeId() {
		return null;
	}

	@Override
	public ReplicatedState getStateMachine(String name, boolean persisted) {
		return null;
	}

	@Override
	public void removeStateMachine(String name) {

	}
}
