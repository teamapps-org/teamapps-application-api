package org.teamapps.application.ux.localize;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.universaldb.index.translation.TranslatableText;

import java.util.function.Function;

public class TranslatableFieldBuilder {


	private final ApplicationInstanceData applicationInstanceData;
	private final TranslatableFieldType fieldType;

	private boolean showFieldMessage;
	private Function<TranslatableText, TranslatableText> translationFunction;
	private int fieldHeight;

	public TranslatableFieldBuilder(TranslatableFieldType fieldType, ApplicationInstanceData applicationInstanceData) {
		this.fieldType = fieldType;
		this.applicationInstanceData = applicationInstanceData;
	}

	public TranslatableFieldBuilder showFieldMessage(boolean showFieldMessage) {
		this.showFieldMessage = showFieldMessage;
		return this;
	}

	public TranslatableFieldBuilder fieldHeight(int fieldHeight) {
		this.fieldHeight = fieldHeight;
		return this;
	}

	public TranslatableFieldBuilder withTranslation(Function<TranslatableText, TranslatableText> translationFunction) {
		this.translationFunction = translationFunction;
		return this;
	}

	public TranslatableFieldType getFieldType() {
		return fieldType;
	}

	public ApplicationInstanceData getApplicationInstanceData() {
		return applicationInstanceData;
	}

	public boolean isShowFieldMessage() {
		return showFieldMessage;
	}

	public Function<TranslatableText, TranslatableText> getTranslationFunction() {
		return translationFunction;
	}

	public int getFieldHeight() {
		return fieldHeight;
	}
}
