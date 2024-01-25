/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2024 TeamApps.org
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

import org.teamapps.cluster.state.*;
import org.teamapps.message.protocol.message.Message;
import org.teamapps.message.protocol.model.PojoObjectDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReplicatedStateMachine implements ReplicatedStateHandler {

	private final ReplicatedState replicatedState;
	private final Map<String, ReplicatedList<? extends Message>> replicatedListByName = new HashMap<>();
	private final Map<String, ReplicatedProperty<? extends Message>> replicatedPropertyByName = new HashMap<>();
	private final List<StateUpdateMessage> preparedUpdates = new ArrayList<>();
	private final List<ReplicatedStateTransactionRule> transactionRules = new ArrayList<>();


	public ReplicatedStateMachine(ReplicatedState replicatedState) {
		this.replicatedState = replicatedState;
	}

	public synchronized <TYPE extends Message> ReplicatedList<TYPE> getOrCreateList(String name, PojoObjectDecoder<TYPE> messageDecoder, Function<TYPE, String> typeToIdFunction) {
		return (ReplicatedList<TYPE>) replicatedListByName.computeIfAbsent(name, s -> new ReplicatedList<>(replicatedState, name, messageDecoder, typeToIdFunction, preparedUpdates, transactionRules));
	}

	public synchronized <TYPE extends Message> ReplicatedProperty<TYPE> getOrCreateProperty(String name, PojoObjectDecoder<TYPE> messageDecoder) {
		return (ReplicatedProperty<TYPE>) replicatedPropertyByName.computeIfAbsent(name, s -> new ReplicatedProperty<>(replicatedState, s, messageDecoder, preparedUpdates));
	}

	public void executePreparedUpdates() {
		replicatedState.executeStateMachineUpdate(new StateUpdate(replicatedState.getName(), preparedUpdates, transactionRules));
		preparedUpdates.clear();
		transactionRules.clear();
	}

	private ReplicatedList<? extends Message> getReplicatedList(String name) {
		return replicatedListByName.get(name);
	}

	private ReplicatedProperty<? extends Message> getReplicatedProperty(String name) {
		return replicatedPropertyByName.get(name);
	}

	@Override
	public void handleStateUpdated(String stateId, Message state) {
		ReplicatedProperty<? extends Message> replicatedProperty = getReplicatedProperty(stateId);
		if (replicatedProperty != null) {
			replicatedProperty.handleSetState(state);
		}
	}

	@Override
	public void handleEntryAdded(String list, Message message) {
		ReplicatedList<? extends Message> replicatedList = getReplicatedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryAdded(message);
		}
	}

	@Override
	public void handleEntryRemoved(String list, Message message) {
		ReplicatedList<? extends Message> replicatedList = getReplicatedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryRemoved(message);
		}
	}

	@Override
	public void handleEntryUpdated(String list, Message currentState, Message previousState) {
		ReplicatedList<? extends Message> replicatedList = getReplicatedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryUpdated(currentState);
		}
	}

	@Override
	public void handleAllEntriesRemoved(String list) {
		ReplicatedList<? extends Message> replicatedList = getReplicatedList(list);
		if (replicatedList != null) {
			replicatedList.handleAllEntriesRemoved();
		}

	}

	@Override
	public void handleFireAndForget(String list, Message message) {
		ReplicatedList<? extends Message> replicatedList = getReplicatedList(list);
		if (replicatedList != null) {
			replicatedList.handleFireAndForget(message);
		}
	}

	@Override
	public void handleStateMachineRemoved() {

	}
}
