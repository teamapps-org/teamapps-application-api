package org.teamapps.application.api.state;

import org.teamapps.cluster.state.LocalState;
import org.teamapps.cluster.state.ReplicatedState;
import org.teamapps.cluster.state.ReplicatedStateHandler;
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.List;

public class MultiStateHandler implements ReplicatedStateHandler {

	private List<ReplicatedStateHandler> stateHandlers = new ArrayList<>();
	private ReplicatedState replicatedState;

	public MultiStateHandler(String name) {
		LocalState localState = new LocalState(name);
		localState.setHandler(this);
		replicatedState = localState;
	}

	public ReplicatedState getReplicatedState() {
		return replicatedState;
	}

	public synchronized void addStateHandler(ReplicatedStateHandler stateHandler, SessionContext context) {
		stateHandlers.add(stateHandler);
		context.onDestroyed.addListener(() -> removeStateHandler(stateHandler));
	}

	private synchronized void removeStateHandler(ReplicatedStateHandler stateHandler) {
		stateHandlers.remove(stateHandler);
	}

	@Override
	public synchronized void handleStateUpdated(String stateId, MessageObject state) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleStateUpdated(stateId, state));
	}

	@Override
	public synchronized void handleEntryAdded(String list, MessageObject message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryAdded(list, message));
	}

	@Override
	public synchronized void handleEntryRemoved(String list, MessageObject message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryRemoved(list, message));
	}

	@Override
	public synchronized void handleEntryUpdated(String list, MessageObject currentState, MessageObject previousState) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryUpdated(list, currentState, previousState));
	}

	@Override
	public synchronized void handleAllEntriesRemoved(String list) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleAllEntriesRemoved(list));
	}

	@Override
	public synchronized void handleFireAndForget(String list, MessageObject message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleFireAndForget(list, message));
	}

	@Override
	public synchronized void handleStateMachineRemoved() {
		stateHandlers.forEach(ReplicatedStateHandler::handleStateMachineRemoved);
	}
}
