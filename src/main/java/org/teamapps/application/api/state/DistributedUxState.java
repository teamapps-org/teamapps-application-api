package org.teamapps.application.api.state;

import org.teamapps.cluster.protocol.test.Participant;
import org.teamapps.cluster.state.*;
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.protocol.schema.ModelCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DistributedUxState implements DistributedStateMachineHandler {

	private DistributedStateMachine distributedStateMachine;
	private final Map<String, DistributedList<? extends MessageObject>> distributedListByName = new HashMap<>();
	private final Map<String, DistributedState<? extends MessageObject>> distributedStateByName = new HashMap<>();
	private final List<StateMachineUpdateMessage> preparedUpdates = new ArrayList<>();


	public void setDistributedStateMachine(DistributedStateMachine distributedStateMachine) {
		this.distributedStateMachine = distributedStateMachine;
	}

	public <TYPE extends MessageObject> DistributedList<TYPE> createList(String name, String modelUuid, Function<TYPE, String> typeToIdFunction, ModelCollection modelCollection) {
		DistributedList<TYPE> distributedList = new DistributedList<>(distributedStateMachine, name, modelUuid, typeToIdFunction, modelCollection, preparedUpdates);
		distributedListByName.put(name, distributedList);
		return distributedList;
	}

	public <TYPE extends MessageObject> DistributedState<TYPE> createState(String name, String modelUuid, ModelCollection modelCollection) {
		DistributedState<TYPE> distributedState = new DistributedState<>(distributedStateMachine, name, modelUuid, modelCollection, preparedUpdates);
		distributedStateByName.put(name, distributedState);
		return distributedState;
	}

	public void executePreparedUpdates() {
		distributedStateMachine.executeStateMachineUpdate(new StateMachineUpdate(distributedStateMachine.getName(), preparedUpdates));
		preparedUpdates.clear();
	}

	private DistributedList<? extends MessageObject> getDistributedList(String name) {
		return distributedListByName.get(name);
	}

	private DistributedState<? extends MessageObject> getDistributedState(String name) {
		return distributedStateByName.get(name);
	}

	public static void main(String[] args) {
		DistributedUxState distributedUxState = new DistributedUxState();
		distributedUxState.setDistributedStateMachine(new LocalStateMachine("testMachine", distributedUxState));

		DistributedList<Participant> participantList = distributedUxState.createList("test", Participant.OBJECT_UUID, participant -> participant.getId() + "", Participant.getModelCollection());

		participantList.onEntryAdded.addListener(entry -> System.out.println("Added: " + entry));
		participantList.onEntryUpdated.addListener(entry -> System.out.println("Updated: " + entry));
		participantList.onEntryRemoved.addListener(entry -> System.out.println("Removed: " + entry));
		participantList.onAllEntriesRemoved.addListener(Void -> System.out.println("Removed all"));

		participantList.addEntry(new Participant().setId(1));
		participantList.addEntry(new Participant().setId(2));
		participantList.updateEntry(new Participant().setId(2));
		System.out.println("Alle entries:");
		participantList.getEntries().forEach(System.out::println);
		participantList.removeEntry(new Participant().setId(2));
		System.out.println("Alle entries:");
		participantList.getEntries().forEach(System.out::println);
	}


	@Override
	public void handleStateUpdated(String stateId, MessageObject state) {
		DistributedState<? extends MessageObject> distributedState = getDistributedState(stateId);
		if (distributedState != null) {
			distributedState.handleSetState(state);
		}
	}

	@Override
	public void handleEntryAdded(String list, MessageObject message) {
		DistributedList<? extends MessageObject> distributedList = getDistributedList(list);
		if (distributedList != null) {
			distributedList.handleEntryAdded(message);
		}
	}

	@Override
	public void handleEntryRemoved(String list, MessageObject message) {
		DistributedList<? extends MessageObject> distributedList = getDistributedList(list);
		if (distributedList != null) {
			distributedList.handleEntryRemoved(message);
		}
	}

	@Override
	public void handleEntryUpdated(String list, MessageObject currentState, MessageObject previousState) {
		DistributedList<? extends MessageObject> distributedList = getDistributedList(list);
		if (distributedList != null) {
			distributedList.handleEntryUpdated(currentState);
		}
	}

	@Override
	public void handleAllEntriesRemoved(String list) {
		DistributedList<? extends MessageObject> distributedList = getDistributedList(list);
		if (distributedList != null) {
			distributedList.handleAllEntriesRemoved();
		}

	}

	@Override
	public void handleStateMachineRemoved() {

	}
}
