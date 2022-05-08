package org.teamapps.application.api.state;

import org.teamapps.cluster.state.DistributedStateMachine;
import org.teamapps.cluster.state.StateMachineUpdateMessage;
import org.teamapps.event.Event;
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.protocol.schema.ModelCollection;
import org.teamapps.protocol.schema.PojoObjectDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DistributedList<TYPE extends MessageObject> {

	private final DistributedStateMachine distributedStateMachine;
	private final String listName;
	private final Function<TYPE, String> typeToIdFunction;
	private final List<StateMachineUpdateMessage> preparedUpdates;
	private final PojoObjectDecoder<TYPE> messageDecoder;

	public Event<TYPE> onEntryAdded = new Event<>();
	public Event<TYPE> onEntryRemoved = new Event<>();
	public Event<TYPE> onEntryUpdated = new Event<>();
	public Event<Void> onAllEntriesRemoved = new Event<>();
	public Event<Void> onListChanged = new Event<>();


	protected DistributedList(DistributedStateMachine distributedStateMachine, String listName, String modelUuid, Function<TYPE, String> typeToIdFunction, ModelCollection modelCollection, List<StateMachineUpdateMessage> preparedUpdates) {
		this.distributedStateMachine = distributedStateMachine;
		this.listName = listName;
		this.typeToIdFunction = typeToIdFunction;
		this.preparedUpdates = preparedUpdates;
		this.messageDecoder = (PojoObjectDecoder<TYPE>) modelCollection.getMessageDecoder(modelUuid);
	}

	public void prepareAddEntry(TYPE entry) {
		preparedUpdates.add(distributedStateMachine.prepareAddEntry(listName, typeToIdFunction.apply(entry), entry));
	}

	public void prepareUpdateEntry(TYPE entry) {
		preparedUpdates.add(distributedStateMachine.prepareUpdateEntry(listName, typeToIdFunction.apply(entry), entry));
	}

	public void prepareRemoveEntry(String identifier) {
		preparedUpdates.add(distributedStateMachine.prepareRemoveEntry(listName, identifier));
	}

	public void prepareRemoveEntry(TYPE entry) {
		preparedUpdates.add(distributedStateMachine.prepareRemoveEntry(listName, typeToIdFunction.apply(entry)));
	}

	public void prepareRemoveAllEntries() {
		preparedUpdates.add(distributedStateMachine.prepareRemoveAllEntries(listName));
	}

	public void addEntry(TYPE entry) {
		distributedStateMachine.addEntry(listName, typeToIdFunction.apply(entry), entry);
	}

	public void updateEntry(TYPE entry) {
		distributedStateMachine.updateEntry(listName, typeToIdFunction.apply(entry), entry);
	}

	public void removeEntry(String identifier) {
		distributedStateMachine.removeEntry(listName, identifier);
	}

	public void removeEntry(TYPE entry) {
		distributedStateMachine.removeEntry(listName, typeToIdFunction.apply(entry));
	}

	public void removeAllEntries() {
		distributedStateMachine.removeAllEntries(listName);
	}

	public List<TYPE> getEntries() {
		List<MessageObject> entries = distributedStateMachine.getEntries(listName);
		if (entries != null) {
			List<TYPE> list = new ArrayList<>();
			entries.forEach(e -> list.add(remap(e)));
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	public List<TYPE> getEntries(int startIndex, int length) {
		List<MessageObject> entries = distributedStateMachine.getEntries(listName);
		if (entries != null) {
			return entries.stream()
					.skip(startIndex)
					.limit(length)
					.map(this::remap)
					.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	public int getEntryCount() {
		List<MessageObject> entries = distributedStateMachine.getEntries(listName);
		if (entries != null) {
			return entries.size();
		} else {
			return 0;
		}
	}

	protected void handleEntryAdded(MessageObject message) {
		onEntryAdded.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleEntryUpdated(MessageObject message) {
		onEntryUpdated.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleEntryRemoved(MessageObject message) {
		onEntryRemoved.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleAllEntriesRemoved() {
		onAllEntriesRemoved.fire();
		onListChanged.fire();
	}

	protected TYPE remap(MessageObject message) {
		return messageDecoder.remap(message);
	}

}
