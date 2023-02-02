/*-
 * ========================LICENSE_START=================================
 * TeamApps Application API
 * ---
 * Copyright (C) 2020 - 2023 TeamApps.org
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

import org.teamapps.cluster.state.ReplicatedState;
import org.teamapps.cluster.state.ReplicatedStateTransactionRule;
import org.teamapps.cluster.state.StateUpdateMessage;
import org.teamapps.cluster.state.TransactionCompareRule;
import org.teamapps.event.Event;
import org.teamapps.message.protocol.message.Message;
import org.teamapps.message.protocol.model.PojoObjectDecoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReplicatedList<TYPE extends Message> {

	private final ReplicatedState replicatedState;
	private final String listName;
	private final Function<TYPE, String> typeToIdFunction;
	private final List<StateUpdateMessage> preparedUpdates;
	private final PojoObjectDecoder<TYPE> messageDecoder;
	private List<ReplicatedStateTransactionRule> transactionRules;

	public Event<TYPE> onEntryAdded = new Event<>();
	public Event<TYPE> onEntryRemoved = new Event<>();
	public Event<TYPE> onEntryUpdated = new Event<>();
	public Event<Void> onAllEntriesRemoved = new Event<>();
	public Event<Void> onListChanged = new Event<>();
	public Event<TYPE> onFireAndForget = new Event<>();


	protected ReplicatedList(ReplicatedState replicatedState, String listName, PojoObjectDecoder<TYPE> messageDecoder, Function<TYPE, String> typeToIdFunction, List<StateUpdateMessage> preparedUpdates, List<ReplicatedStateTransactionRule> transactionRules) {
		this.replicatedState = replicatedState;
		this.listName = listName;
		this.typeToIdFunction = typeToIdFunction;
		this.preparedUpdates = preparedUpdates;
		this.messageDecoder = messageDecoder;
		this.transactionRules = transactionRules;
	}

	public void addConditionContainsNot(TYPE entry) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, typeToIdFunction.apply(entry), TransactionCompareRule.CONTAINS_NOT, 0));
	}

	public void addConditionContainsNot(String identifier) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, identifier, TransactionCompareRule.CONTAINS_NOT, 0));
	}

	public void addConditionContains(TYPE entry) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, typeToIdFunction.apply(entry), TransactionCompareRule.CONTAINS, 0));
	}

	public void addConditionContains(String identifier) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, identifier, TransactionCompareRule.CONTAINS, 0));
	}

	public void addConditionSizeEquals(int size) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, null, TransactionCompareRule.LIST_SIZE_EQUALS, size));
	}

	public void addConditionSizeGreater(int size) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, null, TransactionCompareRule.LIST_SIZE_GREATER, size));
	}

	public void addConditionSizeSmaller(int size) {
		transactionRules.add(new ReplicatedStateTransactionRule(listName, null, TransactionCompareRule.LIST_SIZE_SMALLER, size));
	}

	public void prepareAddEntry(TYPE entry) {
		preparedUpdates.add(replicatedState.prepareAddEntry(listName, typeToIdFunction.apply(entry), entry));
	}

	public void prepareUpdateOrAddEntry(TYPE entry) {
		preparedUpdates.add(replicatedState.prepareUpdateEntry(listName, typeToIdFunction.apply(entry), entry));
	}

	public void prepareRemoveEntry(String identifier) {
		preparedUpdates.add(replicatedState.prepareRemoveEntry(listName, identifier));
	}

	public void prepareRemoveEntry(TYPE entry) {
		preparedUpdates.add(replicatedState.prepareRemoveEntry(listName, typeToIdFunction.apply(entry)));
	}

	public void prepareRemoveAllEntries() {
		preparedUpdates.add(replicatedState.prepareRemoveAllEntries(listName));
	}

	public void addEntry(TYPE entry) {
		replicatedState.addEntry(listName, typeToIdFunction.apply(entry), entry);
	}

	public void updateOrAddEntry(TYPE entry) {
		replicatedState.updateEntry(listName, typeToIdFunction.apply(entry), entry);
	}

	public void removeEntry(String identifier) {
		replicatedState.removeEntry(listName, identifier);
	}

	public void removeEntry(TYPE entry) {
		replicatedState.removeEntry(listName, typeToIdFunction.apply(entry));
	}

	public void removeAllEntries() {
		replicatedState.removeAllEntries(listName);
	}

	public void fireAndForget(TYPE entry) {
		replicatedState.fireAndForget(listName, entry);
	}

	public TYPE getEntry(String identifier) {
		Message entry = replicatedState.getEntry(listName, identifier);
		if (entry != null) {
			return remap(entry);
		} else {
			return null;
		}
	}

	public boolean contains(TYPE entry) {
		if (entry == null) {
			return false;
		} else {
			String identifier = typeToIdFunction.apply(entry);
			return replicatedState.getEntry(listName, identifier) != null;
		}
	}

	public boolean contains(String identifier) {
		return replicatedState.getEntry(listName, identifier) != null;
	}

	public List<TYPE> getEntries() {
		List<Message> entries = replicatedState.getEntries(listName);
		if (entries != null) {
			List<TYPE> list = new ArrayList<>();
			entries.forEach(e -> list.add(remap(e)));
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	public List<TYPE> getEntries(int startIndex, int length) {
		List<Message> entries = replicatedState.getEntries(listName);
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
		List<Message> entries = replicatedState.getEntries(listName);
		if (entries != null) {
			return entries.size();
		} else {
			return 0;
		}
	}

	protected void handleEntryAdded(Message message) {
		onEntryAdded.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleEntryUpdated(Message message) {
		onEntryUpdated.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleEntryRemoved(Message message) {
		onEntryRemoved.fire(remap(message));
		onListChanged.fire();
	}

	protected void handleAllEntriesRemoved() {
		onAllEntriesRemoved.fire();
		onListChanged.fire();
	}

	protected void handleFireAndForget(Message message) {
		onFireAndForget.fire(remap(message));
	}

	protected TYPE remap(Message message) {
		return message != null ? messageDecoder.remap(message) : null;
	}

}
