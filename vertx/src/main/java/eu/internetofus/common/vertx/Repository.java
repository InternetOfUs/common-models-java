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

package eu.internetofus.common.vertx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import org.tinylog.Logger;

/**
 * A component that manage the persistence of a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Repository {

  /**
   * Name of the field to store the version of the schemas.
   */
  public static final String SCHEMA_VERSION = "schema_version";

  /**
   * The pool of database connections.
   */
  protected MongoClient pool;

  /**
   * The version for the schemas.
   */
  protected String schemaVersion;

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * Create a new service.
   *
   * @param vertx         event bus to use.
   * @param pool          to create the connections.
   * @param schemaVersion version of the schemas stored by this repository.
   */
  public Repository(final Vertx vertx, final MongoClient pool, final String schemaVersion) {

    this.vertx = vertx;
    this.pool = pool;
    this.schemaVersion = schemaVersion;

  }

  /**
   * Search for a page.
   *
   * @param collectionName of the collections that contains the models.
   * @param query          to obtain the components of the page.
   * @param options        to apply to the search.
   * @param resultKey      to store the found models.
   * @param map            function to apply to each found object or {@code null}
   *                       to not modify the components.
   *
   * @return the future found page.
   */
  protected Future<JsonObject> searchPageObject(final String collectionName, final JsonObject query,
      final FindOptions options, final String resultKey, final Consumer<JsonObject> map) {

    return this.pool.count(collectionName, query).compose(countResult -> {

      final var total = countResult.longValue();
      final var offset = options.getSkip();
      final var page = new JsonObject().put("offset", offset).put("total", total);
      if (total == 0 || offset >= total) {

        return Future.succeededFuture(page);

      } else {

        options.getFields().put(SCHEMA_VERSION, false);
        return this.pool.findWithOptions(collectionName, query, options).compose(foundObjects -> {

          if (map != null) {

            foundObjects.stream().forEach(map);
          }
          page.put(resultKey, foundObjects);
          return Future.succeededFuture(page);

        });
      }

    });
  }

  /**
   * Delete one document.
   *
   * @param collectionName of the collections that contains the model to delete.
   * @param query          to to match the document to delete.
   *
   * @return the future result of the delete action.
   */
  protected Future<Void> deleteOneDocument(final String collectionName, final JsonObject query) {

    return this.pool.removeDocument(collectionName, query).compose(result -> {

      if (result.getRemovedCount() != 1) {

        return Future.failedFuture("Not found document to delete");

      } else {

        return Future.succeededFuture();
      }

    });

  }

  /**
   * Delete some document.
   *
   * @param collectionName of the collections that contains the model to delete.
   * @param query          to to match the documents to delete.
   *
   * @return the future result of the delete action.
   */
  protected Future<Void> deleteDocuments(final String collectionName, final JsonObject query) {

    return this.pool.removeDocuments(collectionName, query).compose(result -> {

      if (result.getRemovedCount() < 1) {

        return Future.failedFuture("Not found document to delete");

      } else {

        return Future.succeededFuture();
      }

    });

  }

  /**
   * Update one document.
   *
   * @param collectionName of the collections that contains the model to update.
   * @param query          to to match the document to update.
   * @param updateModel    the new values of the model.
   *
   * @return the future result of the update action.
   */
  protected Future<Void> updateOneDocument(@NotNull final String collectionName, @NotNull final JsonObject query,
      final JsonObject updateModel) {

    return this.upsertOneDocument(collectionName, query, updateModel, false).map(any -> null);

  }

  /**
   * Add or update one document.
   *
   * @param collectionName of the collections that contains the model to update.
   * @param query          to to match the document to update.
   * @param updateModel    the new values of the model.
   * @param upsert         is {@code true} if has to add if not defined.
   *
   * @return the future result of the update action with a {@code null} value if
   *         the model is updated or with the identifier of the added model.
   */
  protected Future<String> upsertOneDocument(@NotNull final String collectionName, @NotNull final JsonObject query,
      final JsonObject updateModel, final boolean upsert) {

    if (updateModel == null) {

      return Future.failedFuture("Not found document to update");

    } else {

      // NO modify the _creationTs.
      updateModel.remove("_creationTs");

      // Create the query to update the model.
      final var setFields = new JsonObject().put(SCHEMA_VERSION, this.schemaVersion);
      final var updateQuery = new JsonObject();
      updateQuery.put("$set", setFields);
      final var unsetFields = new JsonObject();
      for (final String fieldName : updateModel.fieldNames()) {

        final var fieldValue = updateModel.getValue(fieldName);
        if (fieldValue != null) {

          setFields.put(fieldName, fieldValue);

        } else {

          unsetFields.put(fieldName, "");
        }

      }

      if (!unsetFields.isEmpty()) {

        updateQuery.put("$unset", unsetFields);

      }

      final var options = new UpdateOptions().setMulti(false).setUpsert(upsert);
      return this.pool.updateCollectionWithOptions(collectionName, query, updateQuery, options).compose(result -> {

        if (result.getDocModified() != 1) {

          if (upsert) {

            String _id = null;
            final var id = result.getDocUpsertedId();
            if (id != null) {

              _id = id.getString("_id");
            }

            if (_id == null && result.getDocMatched() != 1) {

              return Future.failedFuture("Not added document");

            } else {

              return Future.succeededFuture(_id);
            }

          } else {

            return Future.failedFuture("Not found document to update");
          }

        } else {

          return Future.succeededFuture();
        }

      });

    }
  }

  /**
   * Store one document.
   *
   * @param collectionName of the collections that contains the model to store.
   * @param model          to store.
   * @param map            function to modify the stored document. If it is
   *                       {@code null} no modification is applied.
   *
   * @return the future stored model.
   */
  protected Future<JsonObject> storeOneDocument(@NotNull final String collectionName, @NotNull final JsonObject model,
      final Function<JsonObject, JsonObject> map) {

    model.put(SCHEMA_VERSION, this.schemaVersion);
    return this.pool.insert(collectionName, model).compose(id -> {

      model.remove(SCHEMA_VERSION);
      return this.applyMap(model, map);

    });

  }

  /**
   * Apply a map before return a model.
   *
   * @param model to return.
   * @param map   function to modify the model. If it is {@code null} no
   *              modification is applied.
   *
   * @return the future with the mapped result.
   *
   */
  protected Future<JsonObject> applyMap(final JsonObject model, final Function<JsonObject, JsonObject> map) {

    if (map != null) {

      try {

        final var adaptedModel = map.apply(model);
        return Future.succeededFuture(adaptedModel);

      } catch (final Throwable throwable) {

        return Future.failedFuture(throwable);

      }

    } else {

      return Future.succeededFuture(model);
    }
  }

  /**
   * Find one document.
   *
   * @param collectionName of the collections that contains the model to find.
   * @param query          of the document to find.
   * @param fields         to return.
   * @param map            function to modify the found document. If it is
   *                       {@code null} no modification is applied.
   *
   * @return the found document.
   */
  protected Future<JsonObject> findOneDocument(@NotNull final String collectionName, final JsonObject query,
      final JsonObject fields, final Function<JsonObject, JsonObject> map) {

    JsonObject fieldWithoutSchema;
    if (fields == null) {

      fieldWithoutSchema = new JsonObject();

    } else {

      fieldWithoutSchema = fields;
    }
    fieldWithoutSchema.put(SCHEMA_VERSION, false);
    return this.pool.findOne(collectionName, query, fieldWithoutSchema).compose(foundObject -> {

      if (foundObject == null) {

        return Future.failedFuture("Does not exist a document that match '" + query + "'.");

      } else {

        return this.applyMap(foundObject, map);
      }

    });

  }

  /**
   * Convert a set of values to an object that represents the order to the models
   * to return.
   *
   * @param values     to extract how the query has to be sorted. If has to be
   *                   formed by the name by the key with a prefix than can be
   *                   {@code +} or {-} to order in ascending or descending. If
   *                   you set the key without prefix is order in ascending.
   * @param codePrefix prefix to append to the error code.
   * @param checkKey   function to validate that the key is possible. It receive
   *                   the fields and return the key to sort the field or
   *                   {@code null} if the value is not valid. If it is
   *                   {@code null} it used the current key.
   *
   * @return the object that can be used to sort a query.
   *
   * @throws ValidationErrorException If the values are not right.
   */
  public static JsonObject queryParamToSort(final Iterable<String> values, final String codePrefix,
      final Function<String, String> checkKey) throws ValidationErrorException {

    if (values == null) {

      return null;

    } else {

      final var iter = values.iterator();
      if (!iter.hasNext()) {

        return null;

      } else {

        var sort = new JsonObject();
        for (var i = 0; iter.hasNext(); i++) {

          var value = iter.next();
          if (value == null) {

            throw new ValidationErrorException(codePrefix + "[" + i + "]", "An order item can not be 'null'.");

          } else {

            value = value.trim();
            var order = 1;
            if (value.startsWith("+")) {

              value = value.substring(1);

            } else if (value.startsWith("-")) {

              order = -1;
              value = value.substring(1);
            }

            if (value.length() == 0) {

              throw new ValidationErrorException(codePrefix + "[" + i + "]",
                  "You must to define a field in '" + value + "'.");

            } else {

              var key = value;
              if (checkKey != null) {

                key = checkKey.apply(value);
                if (key == null) {

                  throw new ValidationErrorException(codePrefix + "[" + i + "]",
                      "The field '" + value + "' is not valid.");

                }
              }

              if (sort.containsKey(key)) {

                throw new ValidationErrorException(codePrefix + "[" + i + "]",
                    "The '" + value + "' that represents the fields '" + key + "' is already defined.");

              } else {

                sort = sort.put(key, order);

              }
            }
          }
        }

        return sort;
      }
    }
  }

  /**
   * Create a query to return the document that has a schema version less than the
   * specified.
   *
   * @param version that the document has to be less than.
   *
   * @return the query to return the models that has a different schema version.
   *
   * @see #schemaVersion
   */
  protected JsonObject createQueryToReturnDocumentsWithAVersionLessThan(final String version) {

    final var notExists = new JsonObject().put(SCHEMA_VERSION, new JsonObject().put("$exists", false));
    final var notString = new JsonObject().put(SCHEMA_VERSION,
        new JsonObject().put("$not", new JsonObject().put("$type", "string")));
    final var notEq = new JsonObject().put(SCHEMA_VERSION, new JsonObject().put("$lt", version));
    final var query = new JsonObject().put("$or", new JsonArray().add(notExists).add(notString).add(notEq));
    return query;

  }

  /**
   * Migrate the documents of a collection to the current schema.
   *
   * @param collectionName name of the collection to migrate.
   * @param type           of the documents on the collection.
   * @param version        of the documents to migrate.
   *
   * @param <T>            type of the documents.
   *
   * @return the future that will inform if has migrated or not the documents.
   */
  protected <T extends Model> Future<Void> migrateCollection(final String collectionName, final Class<T> type,
      final String version) {

    final var query = this.createQueryToReturnDocumentsWithAVersionLessThan(version);
    return this.pool.count(collectionName, query).compose(maxDocuments -> {

      if (maxDocuments > 0) {

        Logger.trace("Start to migrate {} documents", maxDocuments);
        final Promise<Void> promise = Promise.promise();
        final var consumerId = UUID.randomUUID().toString();
        final var consumer = this.vertx.eventBus().localConsumer(consumerId);
        consumer.handler(msg -> {

          final var body = msg.body();
          if (body instanceof Number) {

            this.migrateOneDocument(consumerId, collectionName, type, query, ((Number) body).longValue());

          } else {

            if (body instanceof String) {

              promise.fail((String) body);

            } else {

              promise.complete();
            }

            consumer.unregister().onComplete(unregister -> {

              Logger.trace(unregister.cause(), "Finished documents migrations");

            });
          }

        });

        consumer.completionHandler(
            register -> this.migrateOneDocument(consumerId, collectionName, type, query, maxDocuments));
        return promise.future();

      } else {
        // nothing to migrate
        return Future.succeededFuture();
      }

    });

  }

  /**
   * Called when want to migrate one document.
   *
   * @param consumerId     address to send the message to continue migrating.
   * @param collectionName name of the collection to migrate.
   * @param type           of the documents on the collection.
   * @param query          to obtain the documents to migrate.
   * @param maxDocuments   number of document that has to migrate.
   *
   * @param <T>            type of the documents.
   */
  protected <T extends Model> void migrateOneDocument(final String consumerId, final String collectionName,
      final Class<T> type, final JsonObject query, final long maxDocuments) {

    this.findOneDocument(collectionName, query, null, null).onComplete(find -> {

      if (find.failed()) {

        final var cause = find.cause();
        Logger.error(cause, "Cannot find document to migrate");
        this.vertx.eventBus().send(consumerId, cause.getMessage());

      } else {

        final var foundObject = find.result();
        try {

          final var mapper = new ObjectMapper();
          mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
          mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
          mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          final var reader = mapper.readerFor(type);

          @SuppressWarnings("unchecked")
          final var value = (T) reader.readValue(foundObject.encode());
          final var id = foundObject.remove("_id");
          final var updateQuery = new JsonObject().put("_id", id);
          final var model = value.toJsonObject();
          for (final var key : foundObject.fieldNames()) {

            if (!model.containsKey(key)) {

              model.putNull(key);
            }
          }
          this.updateOneDocument(collectionName, updateQuery, model).onComplete(update -> {

            if (update.failed()) {

              final var cause = update.cause();
              Logger.error(cause, "Cannot migrate {}", () -> foundObject.encodePrettily());
              this.vertx.eventBus().send(consumerId, cause.getMessage());

            } else if (maxDocuments == 1) {
              // migrated all the documents
              Logger.trace("Migrated all document");
              this.vertx.eventBus().send(consumerId, null);

            } else {

              final var documentsToMigrate = maxDocuments - 1;
              Logger.trace("Migrated a document. There are {} documents to migrate.", documentsToMigrate);
              this.vertx.eventBus().send(consumerId, documentsToMigrate);
            }

          });

        } catch (final Throwable cause) {

          Logger.error(cause, "Cannot migrate {}", () -> foundObject.encodePrettily());
          this.vertx.eventBus().send(consumerId, cause.getMessage());

        }

      }

    });

  }

  /**
   * Create a the update query for set the schema version.
   *
   * @param version to set.
   *
   * @return the query to update the schema version of the components.
   *
   * @see #schemaVersion
   */
  protected JsonObject createUpdateForSchemaVersion(final String version) {

    return new JsonObject().put("$set", new JsonObject().put(SCHEMA_VERSION, version));

  }

  /**
   * Update all the document of a collection to have the current schema version.
   *
   * @param version        to set.
   * @param collectionName name of the collection to update.
   *
   * @return a future that inform if the documents of the collections are updated
   *         or not.
   *
   */
  protected Future<Void> migrateSchemaVersionOnCollectionTo(final String version, final String collectionName) {

    final var query = this.createQueryToReturnDocumentsWithAVersionLessThan(version);
    final var update = this.createUpdateForSchemaVersion(version);
    return this.updateCollection(collectionName, query, update);

  }

  /**
   * Update all the document of a collection.
   *
   * @param collectionName name of the collection to update.
   * @param query          to the documents to update.
   * @param update         query that specify how to update the documents.
   *
   * @return a future that inform if the documents of the collections are updated
   *         or not.
   *
   */
  protected Future<Void> updateCollection(final String collectionName, final JsonObject query,
      final JsonObject update) {

    final Promise<Void> promise = Promise.promise();
    final var options = new UpdateOptions();
    options.setMulti(true);
    this.pool.updateCollectionWithOptions(collectionName, query, update, options, updated -> {

      if (updated.failed()) {

        final var cause = updated.cause();
        Logger.error(cause, "Cannot update the documents on '{}' to have the schema version '{}'.", collectionName,
            this.schemaVersion);
        promise.fail(cause);

      } else {

        Logger.trace("Updated {} documents on '{}' to have the schema version '{}'.",
            () -> updated.result().getDocModified(), () -> collectionName, () -> this.schemaVersion);
        promise.complete();
      }

    });

    return promise.future();

  }

  /**
   * Search for a page.
   *
   * @param collectionName of the collections that contains the models.
   * @param query          to obtain the components of the page.
   * @param order          to provide the results.
   * @param offset         the index of the first elements to return.
   * @param limit          the number maximum of elements to return.
   * @param elementPath    the name of the element to unwind. It has to be the
   *                       values to access to component on the document.
   *
   * @return the future found page.
   */
  protected Future<JsonObject> aggregatePageObject(@NotNull final String collectionName,
      @NotNull final JsonObject query, @NotNull final JsonObject order, final int offset, final int limit,
      @NotNull final String elementPath) {

    final var splitted = AggregationBuilder.splitElementPath(elementPath);
    return this.countAggregation(collectionName, splitted, query).compose(total -> {

      final Promise<JsonObject> promise = Promise.promise();
      if (total == 0 || total < offset) {

        final var page = new JsonObject().put("offset", offset).put("total", total);
        promise.complete(page);

      } else {

        final var retrievePipeline = new AggregationBuilder().unwindPath(splitted).match(query)
            .sort(order, offset, limit).build();
        final var elements = new JsonArray();
        final var resultKey = splitted[splitted.length - 1];
        final var page = new JsonObject().put("offset", offset).put("total", total).put(resultKey, elements);
        this.pool.aggregate(collectionName, retrievePipeline).handler(value -> {

          var element = value.getJsonObject(splitted[0]);
          for (var i = 1; i < splitted.length; i++) {

            element = element.getJsonObject(splitted[i]);
          }
          elements.add(element);

        }).exceptionHandler(cause -> {

          promise.fail(cause);

        }).endHandler(finishedRetrieve -> {

          if (elements.isEmpty()) {

            page.remove(resultKey);
          }
          promise.complete(page);

        });
      }
      return promise.future();

    });

  }

  /**
   * Count the aggregated documents that match the query.
   *
   * @param collectionName of the collections that contains the models.
   * @param elementPath    the splitted path of the elements.
   * @param query          to satisfy.
   *
   * @return the future of the total documents that match.
   */
  protected Future<Long> countAggregation(@NotNull final String collectionName, @NotNull final String[] elementPath,
      @NotNull final JsonObject query) {

    final Promise<Long> promise = Promise.promise();
    final var countPipeline = new AggregationBuilder().unwindPath(elementPath).match(query).build()
        .add(new JsonObject().put("$count", "total"));
    this.pool.aggregate(collectionName, countPipeline).handler(element -> {

      final var total = element.getLong("total", 0l);
      promise.complete(total);

    }).exceptionHandler(cause -> {

      promise.fail(cause);

    }).endHandler(empty -> {
      // Use this complete when no documents match
      promise.tryComplete(0l);

    });

    return promise.future();

  }

}