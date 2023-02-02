package org.teamapps.application.api.application;

import org.teamapps.message.protocol.message.Message;
import org.teamapps.message.protocol.model.PojoObjectDecoder;
import org.teamapps.universaldb.message.MessageCache;
import org.teamapps.universaldb.message.MessageStore;

import java.io.File;

public interface ApplicationInitializer {

	File getAppBasePath();

	<MESSAGE extends Message> void createMessageStore(String name, PojoObjectDecoder<MESSAGE> messageDecoder, boolean withCache);

	<MESSAGE extends Message> MessageStore<MESSAGE> getMessageStore(String name);
}
