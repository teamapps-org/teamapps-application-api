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

import org.teamapps.cluster.state.ReplicatedState;
import org.teamapps.cluster.state.StateUpdateMessage;
import org.teamapps.event.Event;
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.protocol.schema.ModelCollection;
import org.teamapps.protocol.schema.PojoObjectDecoder;

import java.util.List;

public class ReplicatedProperty<TYPE extends MessageObject> {

	private final ReplicatedState distributedStateMachine;
	private final String stateName;
	private final String modelUuid;
	private final ModelCollection modelCollection;
	private final List<StateUpdateMessage> preparedUpdates;
	private final PojoObjectDecoder<TYPE> messageDecoder;

	public Event<TYPE> onStateChanged = new Event<>();


	protected ReplicatedProperty(ReplicatedState distributedStateMachine, String stateName, String modelUuid, ModelCollection modelCollection, List<StateUpdateMessage> preparedUpdates) {
		this.distributedStateMachine = distributedStateMachine;
		this.stateName = stateName;
		this.modelUuid = modelUuid;
		this.modelCollection = modelCollection;
		this.preparedUpdates = preparedUpdates;
		this.messageDecoder = (PojoObjectDecoder<TYPE>) modelCollection.getMessageDecoder(modelUuid);
	}

	public void prepareUpdateState(TYPE state) {
		preparedUpdates.add(distributedStateMachine.prepareSetState(stateName, state));
	}

	public void setState(TYPE state) {
		distributedStateMachine.setProperty(stateName, state);
	}

	public TYPE getProperty() {
		return remap(distributedStateMachine.getProperty(stateName));
	}

	protected void handleSetState(MessageObject state) {
		onStateChanged.fire(remap(state));
	}

	protected TYPE remap(MessageObject message) {
		return message != null ? messageDecoder.remap(message) : null;
	}

}