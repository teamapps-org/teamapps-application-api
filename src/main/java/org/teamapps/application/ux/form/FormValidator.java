package org.teamapps.application.ux.form;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.Fields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FormValidator {

	private final ApplicationInstanceData applicationInstanceData;
	private Set<AbstractField<?>> fields = new HashSet<>();

	public FormValidator(ApplicationInstanceData applicationInstanceData) {
		this.applicationInstanceData = applicationInstanceData;
	}

	public void addEmail(AbstractField<String> field) {
		field.addValidator(FormValidationUtils.createEmailValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addEmailOrEmpty(AbstractField<String> field) {
		field.addValidator(FormValidationUtils.createEmailOrEmptyValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addPhoneNumber(AbstractField<String> field) {
		field.addValidator(FormValidationUtils.createPhoneNumberValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addPhoneOrEmptyNumber(AbstractField<String> field) {
		field.addValidator(FormValidationUtils.createPhoneNumberOrEmptyValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addNotBlank(AbstractField<String> field) {
		field.addValidator(FormValidationUtils.createNotBlankValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addNotNull(AbstractField field) {
		field.addValidator(FormValidationUtils.createNotNullValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addNotEmptyList(AbstractField field) {
		field.addValidator(FormValidationUtils.createNotEmptyListValidator(applicationInstanceData));
		fields.add(field);
	}

	public void addMinCharacters(AbstractField<String> field, int chars) {
		field.addValidator(FormValidationUtils.createMinCharactersValidator(chars, applicationInstanceData));
		fields.add(field);
	}

	public void addMinCharactersOrEmpty(AbstractField<String> field, int chars) {
		field.addValidator(FormValidationUtils.createMinCharactersOrEmptyValidator(chars, applicationInstanceData));
		fields.add(field);
	}

	public void addMaxCharacters(AbstractField<String> field, int chars) {
		field.addValidator(FormValidationUtils.createMaxCharactersValidator(chars, applicationInstanceData));
		fields.add(field);
	}

	public boolean validate() {
		return Fields.validateAll(new ArrayList<>(fields));
	}
}
