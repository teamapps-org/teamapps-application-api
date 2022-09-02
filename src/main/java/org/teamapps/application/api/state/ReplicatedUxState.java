/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2022 TeamApps.org
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
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.protocol.schema.ModelCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReplicatedUxState implements ReplicatedStateHandler {

	private ReplicatedState replicatedState;
	private final Map<String, ReplicatedList<? extends MessageObject>> distributedListByName = new HashMap<>();
	private final Map<String, ReplicatedProperty<? extends MessageObject>> distributedStateByName = new HashMap<>();
	private final List<StateUpdateMessage> preparedUpdates = new ArrayList<>();


	public void setReplicatedState(ReplicatedState replicatedState) {
		this.replicatedState = replicatedState;
	}

	public <TYPE extends MessageObject> ReplicatedList<TYPE> createList(String name, String modelUuid, Function<TYPE, String> typeToIdFunction, ModelCollection modelCollection) {
		ReplicatedList<TYPE> replicatedList = new ReplicatedList<>(replicatedState, name, modelUuid, typeToIdFunction, modelCollection, preparedUpdates);
		distributedListByName.put(name, replicatedList);
		return replicatedList;
	}

	public <TYPE extends MessageObject> ReplicatedProperty<TYPE> createState(String name, String modelUuid, ModelCollection modelCollection) {
		ReplicatedProperty<TYPE> replicatedProperty = new ReplicatedProperty<>(replicatedState, name, modelUuid, modelCollection, preparedUpdates);
		distributedStateByName.put(name, replicatedProperty);
		return replicatedProperty;
	}

	public void executePreparedUpdates() {
		replicatedState.executeStateMachineUpdate(new StateUpdate(replicatedState.getName(), preparedUpdates));
		preparedUpdates.clear();
	}

	private ReplicatedList<? extends MessageObject> getDistributedList(String name) {
		return distributedListByName.get(name);
	}

	private ReplicatedProperty<? extends MessageObject> getDistributedState(String name) {
		return distributedStateByName.get(name);
	}

	public static void main(String[] args) {
		ReplicatedUxState replicatedUxState = new ReplicatedUxState();
		replicatedUxState.setReplicatedState(new LocalState("testMachine", replicatedUxState));

//		DistributedList<Participant> participantList = distributedUxState.createList("test", Participant.OBJECT_UUID, participant -> participant.getId() + "", Participant.getModelCollection());
//
//		participantList.onEntryAdded.addListener(entry -> System.out.println("Added: " + entry));
//		participantList.onEntryUpdated.addListener(entry -> System.out.println("Updated: " + entry));
//		participantList.onEntryRemoved.addListener(entry -> System.out.println("Removed: " + entry));
//		participantList.onAllEntriesRemoved.addListener(Void -> System.out.println("Removed all"));
//
//		participantList.addEntry(new Participant().setId(1));
//		participantList.addEntry(new Participant().setId(2));
//		participantList.updateEntry(new Participant().setId(2));
//		System.out.println("Alle entries:");
//		participantList.getEntries().forEach(System.out::println);
//		participantList.removeEntry(new Participant().setId(2));
//		System.out.println("Alle entries:");
//		participantList.getEntries().forEach(System.out::println);
	}


	@Override
	public void handleStateUpdated(String stateId, MessageObject state) {
		ReplicatedProperty<? extends MessageObject> replicatedProperty = getDistributedState(stateId);
		if (replicatedProperty != null) {
			replicatedProperty.handleSetState(state);
		}
	}

	@Override
	public void handleEntryAdded(String list, MessageObject message) {
		ReplicatedList<? extends MessageObject> replicatedList = getDistributedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryAdded(message);
		}
	}

	@Override
	public void handleEntryRemoved(String list, MessageObject message) {
		ReplicatedList<? extends MessageObject> replicatedList = getDistributedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryRemoved(message);
		}
	}

	@Override
	public void handleEntryUpdated(String list, MessageObject currentState, MessageObject previousState) {
		ReplicatedList<? extends MessageObject> replicatedList = getDistributedList(list);
		if (replicatedList != null) {
			replicatedList.handleEntryUpdated(currentState);
		}
	}

	@Override
	public void handleAllEntriesRemoved(String list) {
		ReplicatedList<? extends MessageObject> replicatedList = getDistributedList(list);
		if (replicatedList != null) {
			replicatedList.handleAllEntriesRemoved();
		}

	}

	@Override
	public void handleFireAndForget(String list, MessageObject message) {
		ReplicatedList<? extends MessageObject> replicatedList = getDistributedList(list);
		if (replicatedList != null) {
			replicatedList.handleFireAndForget(message);
		}
	}

	@Override
	public void handleStateMachineRemoved() {

	}
}