/*
 * -----------------------------------------------------------------------------
 *
 * Copyright (c) 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * -----------------------------------------------------------------------------
 */

package eu.internetofus.common.vertx;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationErrorException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;

/**
 * A component that manage the persistence of a component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Repository {

  /**
   * Name of the filed to store the version of the schemas.
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
   * Create a new service.
   *
   * @param pool          to create the connections.
   * @param schemaVersion version of the schemas stored by this repository.
   */
  public Repository(final MongoClient pool, final String schemaVersion) {

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
   * @param map            function to apply to each found object or {@code null} to not modify the components.
   * @param searchHandler  handler to manage the result action.
   */
  protected void searchPageObject(final String collectionName, final JsonObject query, final FindOptions options, final String resultKey, final Consumer<JsonObject> map, final Handler<AsyncResult<JsonObject>> searchHandler) {

    this.pool.count(collectionName, query, count -> {

      if (count.failed()) {

        searchHandler.handle(Future.failedFuture(count.cause()));

      } else {

        final var total = count.result().longValue();
        final var offset = options.getSkip();
        final var page = new JsonObject().put("offset", offset).put("total", total);
        if (total == 0 || offset >= total) {

          searchHandler.handle(Future.succeededFuture(page));

        } else {

          options.getFields().put(SCHEMA_VERSION, false);
          this.pool.findWithOptions(collectionName, query, options, find -> {

            if (find.failed()) {

              searchHandler.handle(Future.failedFuture(find.cause()));

            } else {

              final var objects = find.result();
              if (map != null) {

                objects.stream().forEach(map);
              }
              page.put(resultKey, objects);
              searchHandler.handle(Future.succeededFuture(page));
            }

          });

        }

      }
    });
  }

  /**
   * Delete one document.
   *
   * @param collectionName of the collections that contains the model to delete.
   * @param query          to to match the document to delete.
   * @param deleteHandler  handler to manage the delete action.
   */
  protected void deleteOneDocument(final String collectionName, final JsonObject query, final Handler<AsyncResult<Void>> deleteHandler) {

    this.pool.removeDocument(collectionName, query, remove -> {

      if (remove.failed()) {

        deleteHandler.handle(Future.failedFuture(remove.cause()));

      } else if (remove.result().getRemovedCount() != 1) {

        deleteHandler.handle(Future.failedFuture("Not found document to delete"));

      } else {

        deleteHandler.handle(Future.succeededFuture());
      }
    });

  }

  /**
   * Update one document.
   *
   * @param collectionName of the collections that contains the model to update.
   * @param query          to to match the document to update.
   * @param updateModel    the new values of the model.
   * @param updateHandler  handler to manage the update action.
   */
  protected void updateOneDocument(@NotNull final String collectionName, @NotNull final JsonObject query, final JsonObject updateModel, final Handler<AsyncResult<Void>> updateHandler) {

    if (updateModel == null) {

      updateHandler.handle(Future.failedFuture("Not found document to update"));

    } else {

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

      final var options = new UpdateOptions().setMulti(false);
      this.pool.updateCollectionWithOptions(collectionName, query, updateQuery, options, update -> {

        if (update.failed()) {

          updateHandler.handle(Future.failedFuture(update.cause()));

        } else if (update.result().getDocModified() != 1) {

          updateHandler.handle(Future.failedFuture("Not found document to update"));

        } else {

          updateHandler.handle(Future.succeededFuture());
        }
      });

    }
  }

  /**
   * Store one document.
   *
   * @param collectionName of the collections that contains the model to store.
   * @param model          to store.
   * @param map            function to modify the stored document. If it is {@code null} no modification is applied.
   *
   * @param storeHandler   handler to manage the store action.
   */
  protected void storeOneDocument(@NotNull final String collectionName, @NotNull final JsonObject model, final Function<JsonObject, JsonObject> map, final Handler<AsyncResult<JsonObject>> storeHandler) {

    model.put(SCHEMA_VERSION, this.schemaVersion);
    this.pool.insert(collectionName, model, store -> {

      model.remove(SCHEMA_VERSION);
      if (store.failed()) {

        storeHandler.handle(Future.failedFuture(store.cause()));

      } else {

        this.applyMap(model, map, storeHandler);
      }

    });
  }

  /**
   * Apply a map before return a model.
   *
   * @param model   to return.
   * @param map     function to modify the model. If it is {@code null} no modification is applied.
   * @param handler to manage the model to return.
   *
   */
  protected void applyMap(final JsonObject model, final Function<JsonObject, JsonObject> map, final Handler<AsyncResult<JsonObject>> handler) {

    if (map != null) {

      try {

        final var adaptedModel = map.apply(model);
        handler.handle(Future.succeededFuture(adaptedModel));

      } catch (final Throwable throwable) {

        handler.handle(Future.failedFuture(throwable));

      }

    } else {

      handler.handle(Future.succeededFuture(model));
    }
  }

  /**
   * Find one document.
   *
   * @param collectionName of the collections that contains the model to find.
   * @param query          of the document to find.
   * @param fields         to return.
   * @param map            function to modify the found document. If it is {@code null} no modification is applied.
   * @param searchHandler  handler to manage the find action.
   */
  protected void findOneDocument(@NotNull final String collectionName, final JsonObject query, final JsonObject fields, final Function<JsonObject, JsonObject> map, final Handler<AsyncResult<JsonObject>> searchHandler) {

    JsonObject fieldWithoutSchema = null;
    if (fields == null) {

      fieldWithoutSchema = new JsonObject();

    } else {

      fieldWithoutSchema = fields;
    }
    fieldWithoutSchema.put(SCHEMA_VERSION, false);
    this.pool.findOne(collectionName, query, fieldWithoutSchema, search -> {

      if (search.failed()) {

        searchHandler.handle(Future.failedFuture(search.cause()));

      } else {

        final JsonObject value = search.result();
        if (value == null) {

          searchHandler.handle(Future.failedFuture("Does not exist a document that match '" + query + "'."));

        } else {

          this.applyMap(value, map, searchHandler);
        }
      }
    });

  }

  /**
   * Convert a set of values to an object that represents the order to the models to return.
   *
   * @param values     to extract how the query has to be sorted. If has to be formed by the name by the key with a prefix
   *                   than can be {@code +} or {-} to order in ascending or descending. If you set the key without prefix
   *                   is order in ascending.
   * @param codePrefix prefix to append to the error code.
   * @param checkKey   function to validate that the key is possible. It receive the fields and return the key to sort the
   *                   field or {@code null} if the value is not valid. If it is {@code null} it used the current key.
   *
   * @return the object that can be used to sort a query.
   *
   * @throws ValidationErrorException If the values are not right.
   */
  public static JsonObject queryParamToSort(final Iterable<String> values, final String codePrefix, final Function<String, String> checkKey) throws ValidationErrorException {

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

              throw new ValidationErrorException(codePrefix + "[" + i + "]", "You must to define a field in '" + value + "'.");

            } else {

              var key = value;
              if (checkKey != null) {

                key = checkKey.apply(value);
                if (key == null) {

                  throw new ValidationErrorException(codePrefix + "[" + i + "]", "The field '" + value + "' is not valid.");

                }
              }

              if (sort.containsKey(key)) {

                throw new ValidationErrorException(codePrefix + "[" + i + "]", "The '" + value + "' that represents the fields '" + key + "' is already defined.");

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
   * Create a query that return
   *
   * @return the query to return the models that has a different schema version.
   *
   * @see #schemaVersion
   */
  protected JsonObject createQueryToReturnDocumentsThatNotMatchSchemaVersion() {

    final var notExists = new JsonObject().put(SCHEMA_VERSION, new JsonObject().put("$exists", false));
    final var notEq = new JsonObject().put(SCHEMA_VERSION, new JsonObject().put("$not", new JsonObject().put("$eq", this.schemaVersion)));
    return new JsonObject().put("$or", new JsonArray().add(notExists).add(notEq));

  }

  /**
   * Migrate the documents of a collection to the current schema.
   *
   * @param collectionName name of the collection to migrate.
   * @param type           of the documents on the collection. {@code null} use the found element.
   *
   * @param <T>            type of the documents.
   *
   * @return the future that will inform if has migrated or not the documents.
   */
  protected <T extends Model> Future<Void> migrateCollection(final String collectionName, final Class<T> type) {

    final Promise<Void> promise = Promise.promise();
    final JsonObject query = this.createQueryToReturnDocumentsThatNotMatchSchemaVersion();
    this.pool.count(collectionName, query, count -> {

      if (count.failed()) {

        final var cause = count.cause();
        Logger.error(cause, "Cannot obtain the number of document to migrate");
        promise.fail(cause);

      } else {

        final long maxDocuments = count.result();
        if (maxDocuments > 0) {

          Logger.trace("Start to migrate {} documents", maxDocuments);
          this.migrateOneDocument(collectionName, type, query, maxDocuments, promise);

        } else {
          // nothing to migrate
          promise.complete();
        }

      }

    });

    return promise.future();

  }

  /**
   * Called when want to migrate one document.
   *
   * @param collectionName name of the collection to migrate.
   * @param type           of the documents on the collection.
   * @param query          to obtain the documents to migrate.
   * @param maxDocuments   number of document that has to migrate.
   * @param promise        to inform of the migration process.
   *
   * @param <T>            type of the documents.
   */
  protected <T extends Model> void migrateOneDocument(final String collectionName, final Class<T> type, final JsonObject query, final long maxDocuments, final Promise<Void> promise) {

    this.findOneDocument(collectionName, query, null, null, search -> {

      if (search.failed()) {

        final var cause = search.cause();
        Logger.error(cause, "Cannot obtain a document to migrate");
        promise.fail(cause);

      } else {

        final var result = search.result();
        final var id = result.remove("_id");
        try {

          final var mapper = new ObjectMapper();
          mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
          mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
          mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          final var reader = mapper.readerFor(type);
          final T value = reader.readValue(result.encode());
          final JsonObject updateQuery = new JsonObject().put("_id", id);
          final JsonObject model = value.toJsonObject();
          for (final var key : result.fieldNames()) {

            if (!model.containsKey(key)) {

              model.putNull(key);
            }
          }
          this.updateOneDocument(collectionName, updateQuery, model, update -> {

            if (update.failed()) {

              final var cause = update.cause();
              Logger.error(cause, "Cannot update a migrated document.\nCurrent:{}\nNew:{}\n", () -> result.encodePrettily(), () -> value);
              promise.fail(cause);

            } else if (maxDocuments == 1) {
              // migrated all the documents
              Logger.trace("Migrated all document");
              promise.complete();

            } else {

              final var documentsToMigrate = maxDocuments - 1;
              Logger.trace("Migrated a document. There are {} documents to migrate.", documentsToMigrate);
              this.migrateOneDocument(collectionName, type, query, documentsToMigrate, promise);
            }

          });

        } catch (final Throwable cause) {

          Logger.error(cause, "Cannot migrate {}", () -> result.encodePrettily());
          promise.fail(cause);

        }

      }

    });

  }

  /**
   * Update all the document of a collection to have the current schema version.
   *
   * @param collectionName name of the collection to update.
   *
   * @return a future that inform if the documents of the collections are updated or not.
   *
   */
  protected Future<Void> updateSchemaVersionOnCollection(final String collectionName) {

    final Promise<Void> promise = Promise.promise();
    final var query = this.createQueryToReturnDocumentsThatNotMatchSchemaVersion();
    final var update = new JsonObject().put("$set", new JsonObject().put(SCHEMA_VERSION, this.schemaVersion));
    final var options = new UpdateOptions();
    options.setMulti(true);
    this.pool.updateCollectionWithOptions(collectionName, query, update, options, updated -> {

      if (updated.failed()) {

        final var cause = updated.cause();
        Logger.error(cause, "Cannot update the documents on '{}' to have the schema version '{}'.", collectionName, this.schemaVersion);
        promise.fail(cause);

      } else {

        Logger.trace("Updated {} documents on '{}' to have the schema version '{}'.", () -> updated.result().getDocModified(), () -> collectionName, () -> this.schemaVersion);
        promise.complete();
      }

    });

    return promise.future();

  }

}