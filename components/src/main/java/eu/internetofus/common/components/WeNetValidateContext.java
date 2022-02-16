/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */
package eu.internetofus.common.components;

import eu.internetofus.common.components.models.CommunityProfile;
import eu.internetofus.common.components.models.SocialNetworkRelationshipType;
import eu.internetofus.common.components.models.Task;
import eu.internetofus.common.components.models.TaskType;
import eu.internetofus.common.components.models.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.components.service.App;
import eu.internetofus.common.components.service.WeNetService;
import eu.internetofus.common.components.task_manager.WeNetTaskManager;
import eu.internetofus.common.model.ValidateContext;
import eu.internetofus.common.vertx.OpenAPIValidator;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * The cache of the values that has been used on the validation.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetValidateContext implements ValidateContext<WeNetValidateContext> {

  /**
   * The error code of the context.
   */
  protected String errorCode;

  /**
   * Event bus to use.
   */
  protected Vertx vertx;

  /**
   * The cache with the models identifiers that has been found.
   */
  protected Set<String> idsCache;

  /**
   * The cache with the loaded models.
   */
  protected Map<String, Object> modelsCache;

  /**
   * Create a new context.
   *
   * @param errorCode   of the context.
   * @param vertx       event bus to use.
   * @param idsCache    the cache with the found identifiers.
   * @param modelsCache the cache with the loaded models.
   */
  protected WeNetValidateContext(final String errorCode, final Vertx vertx, final Set<String> idsCache,
      final Map<String, Object> modelsCache) {

    this.errorCode = errorCode;
    this.vertx = vertx;
    this.idsCache = idsCache;
    this.modelsCache = modelsCache;
  }

  /**
   * Create a new context.
   *
   * @param errorCode of the context.
   * @param vertx     event bus to use.
   */
  public WeNetValidateContext(final String errorCode, final Vertx vertx) {

    this(errorCode, vertx, Collections.synchronizedSet(new HashSet<>()), Collections.synchronizedMap(new HashMap<>()));
  }

  /**
   * GEnerate.
   *
   * @param id   identifier of the model.
   * @param type of the model.
   *
   * @return the
   */
  protected String generateKey(final String id, final Class<?> type) {

    final var builder = new StringBuilder();
    builder.append(type.getName());
    builder.append("#");
    builder.append(id);
    return builder.toString();
  }

  /**
   * Get a cached model.
   *
   * @param id   of the model.
   * @param type of the model.
   * @param <T>  type of the model.
   *
   * @return the cached model or {@code null} if it is not defined.
   */
  @SuppressWarnings("unchecked")
  public <T> T getModel(final String id, final Class<T> type) {

    if (id == null || type == null) {

      return null;

    } else {

      final var key = this.generateKey(id, type);
      return (T) this.modelsCache.get(key);

    }

  }

  /**
   * Add a model into the cache.
   *
   * @param id    of the model.
   * @param model to store.
   */
  public void setModel(final String id, final Object model) {

    if (id != null && model != null) {

      final var key = this.generateKey(id, model.getClass());
      this.modelsCache.put(key, model);

    }

  }

  /**
   * Check if a model exist.
   *
   * @param id   of the model.
   * @param type of the model.
   *
   * @return {@code true} if the model is cached.
   */
  public boolean existModel(final String id, final Class<?> type) {

    if (id == null || type == null) {

      return false;

    } else {

      final var key = this.generateKey(id, type);
      return this.modelsCache.containsKey(key);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see #errorCode
   */
  @Override
  public String errorCode() {

    return this.errorCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WeNetValidateContext createContextWithErrorCode(final String errorCode) {

    return new WeNetValidateContext(errorCode, this.vertx, this.idsCache, this.modelsCache);
  }

  /**
   * Check if a field contains a defined model.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param type   of model to search.
   * @param search function to check if the model exist.
   * @param future to compose if the profile is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedIdField(final String name, final String id, final Class<?> type,
      final Function<String, Future<Boolean>> search, final Future<Void> future) {

    final var key = this.generateKey(id, type);
    if (this.idsCache.contains(key)) {

      return future;

    } else {

      return future.compose(empty -> search.apply(id).transform(defined -> {
        if (defined.failed()) {

          return this.failField(name, defined.cause());

        } else if (defined.result() == true) {

          this.idsCache.add(key);
          return Future.succeededFuture();

        } else {

          return this.failField(name, "The '" + id + "' is not associated to any model.");
        }
      }));
    }

  }

  /**
   * Check if a field contains a defined model.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param type   of model to search.
   * @param search function to check if the model exist.
   * @param future to compose if the model is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateNotDefinedIdField(final String name, final String id, final Class<?> type,
      final Function<String, Future<Boolean>> search, final Future<Void> future) {

    final var key = this.generateKey(id, type);
    if (this.idsCache.contains(key)) {

      return this.failField(name, "The '" + id + "' is associated to an existing model.");

    } else {

      return future.compose(empty -> search.apply(id).transform(defined -> {
        if (defined.failed()) {

          return this.failField(name, defined.cause().getMessage());

        } else if (defined.result() == true) {

          this.idsCache.add(key);
          return this.failField(name, "The '" + id + "' is associated to an existing model.");

        } else {

          return Future.succeededFuture();
        }
      }));
    }

  }

  /**
   * Validate that a field contain the identifier of an application.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the application is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedAppIdField(final String name, final String id, final Future<Void> future) {

    return this.validateDefinedIdField(name, id, App.class, WeNetService.createProxy(this.vertx)::isAppDefined, future);

  }

  /**
   * Validate that a field contain the identifier of a profile.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the profile is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedProfileIdField(final String name, final String id, final Future<Void> future) {

    return this.validateDefinedIdField(name, id, WeNetUserProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::isProfileDefined, future);

  }

  /**
   * Validate that a field contain the identifier of a community.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the community is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedCommunityIdField(final String name, final String id, final Future<Void> future) {

    return this.validateDefinedIdField(name, id, CommunityProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::isCommunityDefined, future);

  }

  /**
   * Validate that a field is not equals to any defined community identifier.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the community is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateNotDefinedCommunityIdField(final String name, final String id,
      final Future<Void> future) {

    return this.validateNotDefinedIdField(name, id, CommunityProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::isCommunityDefined, future);
  }

  /**
   * Validate that a set of profiles are right.
   *
   * @param name   of the field to validate.
   * @param ids    that has to be defined.
   * @param future to compose if the profiles are defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedProfileIdsField(final String name, final Iterable<String> ids,
      final Future<Void> future) {

    return this.validateDefinedIdsField(name, ids, WeNetUserProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::isProfileDefined, future);
  }

  /**
   * Validate that a set of task types are right.
   *
   * @param name   of the field to validate.
   * @param ids    that has to be defined.
   * @param future to compose if the task types are defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedTaskTypeIdsField(final String name, final Iterable<String> ids,
      final Future<Void> future) {

    return this.validateDefinedIdsField(name, ids, TaskType.class,
        WeNetTaskManager.createProxy(this.vertx)::isTaskTypeDefined, future);
  }

  /**
   * Check if a field contains a defined model.
   *
   * @param name   of the field to validate.
   * @param ids    identifiers of the models.
   * @param type   of model to search.
   * @param search function to check if the models exist.
   * @param future to compose if the models are defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedIdsField(final String name, final Iterable<String> ids, final Class<?> type,
      final Function<String, Future<Boolean>> search, final Future<Void> future) {

    @SuppressWarnings("rawtypes")
    final List<Future> futures = new ArrayList<>();
    final var alreadyDefined = new HashMap<String, Integer>();
    final var iter = ids.iterator();
    for (var i = 0; iter.hasNext(); i++) {

      final var id = iter.next();
      if (id == null) {

        iter.remove();

      } else if (alreadyDefined.containsKey(id)) {

        final var index = alreadyDefined.get(id);
        return this.failFieldElement(name, i, "The '" + id + "' is already defied at' " + index + "'.");

      } else {

        alreadyDefined.put(id, i);
        final var key = this.generateKey(id, type);
        if (!this.idsCache.contains(key)) {

          final var index = i;
          futures.add(search.apply(id).transform(defined -> {
            if (defined.failed()) {

              return this.failFieldElement(name, index, defined.cause());

            } else if (defined.result() == true) {

              this.idsCache.add(key);
              return Future.succeededFuture();

            } else {

              return this.failFieldElement(name, index, "The '" + id + "' is not associated to any model.");
            }
          }));
        }
      }
    }

    if (futures.isEmpty()) {

      return future;

    } else {

      futures.add(0, future);
      return CompositeFuture.all(futures).map(any -> null);
    }

  }

  /**
   * Validate that a field is not a defined profile.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the profile is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateNotDefinedProfileIdField(final String name, final String id, final Future<Void> future) {

    return this.validateNotDefinedIdField(name, id, WeNetUserProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::isProfileDefined, future);

  }

  /**
   * Validate that a field is not a defined task.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the task is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateNotDefinedTaskIdField(final String name, final String id, final Future<Void> future) {

    return this.validateNotDefinedIdField(name, id, Task.class, WeNetTaskManager.createProxy(this.vertx)::isTaskDefined,
        future);

  }

  /**
   * Validate that a field is not a defined task type.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the task type is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateNotDefinedTaskTypeIdField(final String name, final String id, final Future<Void> future) {

    return this.validateNotDefinedIdField(name, id, TaskType.class,
        WeNetTaskManager.createProxy(this.vertx)::isTaskTypeDefined, future);

  }

  /**
   * Validate that a field is a valid OpenAPI specification.
   *
   * @param name          of the field to validate.
   * @param specification to validate.
   * @param future        to compose if the task type is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateOpenAPISpecificationField(final String name, final JsonObject specification,
      final Future<Void> future) {

    final var code = this.fieldErrorCode(name);
    return future.compose(empty -> OpenAPIValidator.validateSpecification(code, this.vertx, specification));
  }

  /**
   * Validate that a field is a valid composed OpenAPI specification.
   *
   * @param name          of the field to validate.
   * @param specification to validate.
   * @param future        to compose if the task type is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateComposedOpenAPISpecificationField(final String name, final JsonObject specification,
      final Future<Void> future) {

    final var code = this.fieldErrorCode(name);
    return future.compose(empty -> OpenAPIValidator.validateComposedSpecification(code, this.vertx, specification));
  }

  /**
   * Validate that a field is a valid OpenAPI value.
   *
   * @param name          of the field to validate.
   * @param value         to validate.
   * @param specification of the value.
   * @param <T>           type of value to validate.
   *
   * @return the future with the validation result.
   */
  public <T> Future<T> validateOpenAPIValueField(final String name, final T value, final JsonObject specification) {

    final var code = this.fieldErrorCode(name);
    return OpenAPIValidator.validateValue(code, this.vertx, specification, value);
  }

  /**
   * Validate that a field contains a defined task type.
   *
   * @param name       of the field to validate.
   * @param taskTypeId identifier of the task type.
   *
   * @return the future with the validation result.
   */
  public Future<TaskType> validateDefinedTaskTypeByIdField(final String name, final String taskTypeId) {

    return this.validateDefinedModelByIdField(name, taskTypeId, TaskType.class,
        WeNetTaskManager.createProxy(this.vertx)::retrieveTaskType);

  }

  /**
   * Validate that a field contains a defined task.
   *
   * @param name   of the field to validate.
   * @param taskId identifier of the task .
   *
   * @return the future with the validation result.
   */
  public Future<Task> validateDefinedTaskByIdField(final String name, final String taskId) {

    return this.validateDefinedModelByIdField(name, taskId, Task.class,
        WeNetTaskManager.createProxy(this.vertx)::retrieveTask);

  }

  /**
   * Validate that a field contains a defined profile.
   *
   * @param name      of the field to validate.
   * @param profileId identifier of the profile .
   *
   * @return the future with the validation result.
   */
  public Future<WeNetUserProfile> validateDefinedProfileByIdField(final String name, final String profileId) {

    return this.validateDefinedModelByIdField(name, profileId, WeNetUserProfile.class,
        WeNetProfileManager.createProxy(this.vertx)::retrieveProfile);

  }

  /**
   * Validate that a field contains a defined model.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param type   of model to search.
   * @param search function to get the model by its identifier.
   * @param <T>    type of model to check if it is defined.
   *
   * @return the future with the validation result.
   */
  public <T> Future<T> validateDefinedModelByIdField(final String name, final String id, final Class<T> type,
      final Function<String, Future<T>> search) {

    final var key = this.generateKey(id, type);
    @SuppressWarnings("unchecked")
    final var model = (T) this.modelsCache.get(key);
    if (model == null) {

      return search.apply(id).transform(found -> {

        if (found.failed()) {

          return this.failField(name, "The '" + id + "' is not associated to any model.", found.cause());

        } else {

          final var result = found.result();
          this.idsCache.add(key);
          this.modelsCache.put(key, result);
          return Future.succeededFuture(result);
        }
      });

    } else {

      return Future.succeededFuture(model);

    }
  }

  /**
   * Validate that a field is a defined task type.
   *
   * @param name   of the field to validate.
   * @param id     identifier of the model.
   * @param future to compose if the task type is defined or not.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateDefinedTaskTypeIdField(final String name, final String id, final Future<Void> future) {

    return this.validateDefinedIdField(name, id, TaskType.class,
        WeNetTaskManager.createProxy(this.vertx)::isTaskTypeDefined, future);

  }

  /**
   * Check that exist a social network relationship between some users.
   *
   * @param name     of the field to validate.
   * @param appId    application identifier to match in the relationships to
   *                 return.
   * @param sourceId user identifier to match the source of the relationships to
   *                 return.
   * @param targetId user identifier to match the target of the relationships to
   *                 return.
   * @param type     to match in the relationships to return.
   * @param future   to compose if the social network relationship exist.
   *
   * @return the future with the validation result.
   */
  public Future<Void> validateExistSocialNetworkRelationshipField(final String name, final String appId,
      final String sourceId, final String targetId, final SocialNetworkRelationshipType type,
      final Future<Void> future) {

    return future.compose(empty -> {

      String typeName = null;
      if (type != null) {

        typeName = type.name();
      }
      return WeNetProfileManager.createProxy(this.vertx)
          .retrieveSocialNetworkRelationshipsPage(appId, sourceId, targetId, typeName, null, 0, 0).transform(page -> {

            if (page.failed() || page.result().total != 1) {

              return this.failField(name, "The '" + type + "' is not defined on the app '" + appId
                  + "' by the source user '" + sourceId + "' with the target user '" + targetId + "'.");

            } else {

              return Future.succeededFuture();
            }

          });

    });
  }

}
