package org.teamapps.application.api.ui;

import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.linkbutton.LinkButton;

public interface TranslationKeyField {

	AbstractField<String> getSelectionField();

	AbstractField<String> getKeyDisplayField();

	LinkButton getKeyLinkButton();

	void setKey(String key);

	String getKey();
}
