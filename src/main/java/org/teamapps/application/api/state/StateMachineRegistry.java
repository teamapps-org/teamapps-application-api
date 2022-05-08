package org.teamapps.application.api.state;

import org.teamapps.cluster.state.DistributedStateMachine;
import org.teamapps.cluster.state.DistributedStateMachineRegistry;

public class StateMachineRegistry implements DistributedStateMachineRegistry {


	@Override
	public DistributedStateMachine getStateMachine(String name, boolean persisted) {
		return null;
	}

	@Override
	public void removeStateMachine(String name) {

	}
}
