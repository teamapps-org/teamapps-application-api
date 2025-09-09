package org.teamapps.application.ui.model;

import org.teamapps.application.api.application.ApplicationInstanceData;
import org.teamapps.application.api.application.entity.EntityUpdateType;
import org.teamapps.application.ui.notification.NotificationUpdateController;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.event.Event;
import org.teamapps.model.controlcenter.OrganizationUnitView;

import java.util.function.*;

public interface LifecycleEntityModel<ENTITY> extends EntitySelectionModel<ENTITY> {

	NotificationUpdateController<ENTITY> getNotificationUpdateController(Function<ENTITY, OrganizationUnitView> unitByEntityFunction);

	Event<EntityUpdateType> getOnSelectedEntityExternallyChanged();

	Supplier<ENTITY> getNewEntitySupplier();

	ObservableValue<ENTITY> getSelectedEntity();

	String getEntityTitle(ENTITY entity, ApplicationInstanceData applicationInstanceData);

	void setDeletionDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler);

	void setRestoreDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler);

	void setSaveDialogue(BiConsumer<ENTITY, Runnable> dialogueHandler);

	void addDeletionValidator(Function<ENTITY, ValidationMessage> validator);

	void addRestoreValidator(Function<ENTITY, ValidationMessage> validator);

	void addSaveValidator(Function<ENTITY, ValidationMessage> validator);

	void addCreationListener(Consumer<ENTITY> listener);

	void addUpdateListener(Consumer<ENTITY> listener);

	void addDeletionListener(Consumer<ENTITY> listener);

	void addRestoreListener(Consumer<ENTITY> listener);

	void addBeforeUpdateAndAfterUpdateHandler(BiFunction<ENTITY, ENTITY, Runnable> beforeUpdateAndAfterHandler);

	void saveEntity(ENTITY entity);

	void saveEntity(ENTITY entity, Runnable onSuccessRunnable);

	void deleteEntity(ENTITY entity);

	void deleteEntity(ENTITY entity, Runnable onSuccessRunnable);

	void restoreEntity(ENTITY entity);

	void restoreEntity(ENTITY entity, Runnable onSuccessRunnable);
}
