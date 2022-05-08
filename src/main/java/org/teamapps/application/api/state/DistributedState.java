package org.teamapps.application.api.state;

import org.teamapps.cluster.state.DistributedStateMachine;
import org.teamapps.cluster.state.StateMachineUpdateMessage;
import org.teamapps.event.Event;
import org.teamapps.protocol.schema.MessageObject;
import org.teamapps.protocol.schema.ModelCollection;
import org.teamapps.protocol.schema.PojoObjectDecoder;

import java.util.List;

public class DistributedState<TYPE extends MessageObject> {

	private final DistributedStateMachine distributedStateMachine;
	private final String stateName;
	private final String modelUuid;
	private final ModelCollection modelCollection;
	private final List<StateMachineUpdateMessage> preparedUpdates;
	private final PojoObjectDecoder<TYPE> messageDecoder;

	public Event<TYPE> onStateChanged = new Event<>();


	protected DistributedState(DistributedStateMachine distributedStateMachine, String stateName, String modelUuid, ModelCollection modelCollection, List<StateMachineUpdateMessage> preparedUpdates) {
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
		distributedStateMachine.setState(stateName, state);
	}

	protected void handleSetState(MessageObject state) {
		onStateChanged.fire(remap(state));
	}

	protected TYPE remap(MessageObject message) {
		return messageDecoder.remap(message);
	}

}
