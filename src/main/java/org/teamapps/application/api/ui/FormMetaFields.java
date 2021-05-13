package org.teamapps.application.api.ui;

import org.teamapps.universaldb.pojo.Entity;
import org.teamapps.ux.component.form.ResponsiveForm;

public interface FormMetaFields {

	void addMetaFields(ResponsiveForm<?> form);

	void updateEntity(Entity<?> entity);
}
