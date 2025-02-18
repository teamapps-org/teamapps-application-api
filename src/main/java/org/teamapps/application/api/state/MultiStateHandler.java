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
package org.teamapps.application.api.state;

import org.teamapps.cluster.state.LocalState;
import org.teamapps.cluster.state.ReplicatedState;
import org.teamapps.cluster.state.ReplicatedStateHandler;
import org.teamapps.message.protocol.message.Message;
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
		if (context != null) {
			context.onDestroyed.addListener(() -> removeStateHandler(stateHandler));
		}
	}

	private synchronized void removeStateHandler(ReplicatedStateHandler stateHandler) {
		stateHandlers.remove(stateHandler);
	}

	@Override
	public synchronized void handleStateUpdated(String stateId, Message state) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleStateUpdated(stateId, state));
	}

	@Override
	public synchronized void handleEntryAdded(String list, Message message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryAdded(list, message));
	}

	@Override
	public synchronized void handleEntryRemoved(String list, Message message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryRemoved(list, message));
	}

	@Override
	public synchronized void handleEntryUpdated(String list, Message currentState, Message previousState) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleEntryUpdated(list, currentState, previousState));
	}

	@Override
	public synchronized void handleAllEntriesRemoved(String list) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleAllEntriesRemoved(list));
	}

	@Override
	public synchronized void handleFireAndForget(String list, Message message) {
		stateHandlers.forEach(stateHandler -> stateHandler.handleFireAndForget(list, message));
	}

	@Override
	public synchronized void handleStateMachineRemoved() {
		stateHandlers.forEach(ReplicatedStateHandler::handleStateMachineRemoved);
	}
}
