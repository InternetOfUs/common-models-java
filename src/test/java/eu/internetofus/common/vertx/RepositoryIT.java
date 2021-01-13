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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.Containers;
import eu.internetofus.common.components.DummyComplexModel;
import eu.internetofus.common.components.DummyComplexModelTest;
import eu.internetofus.common.components.DummyModel;
import eu.internetofus.common.components.Model;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test {@link Repository}
 *
 * @see Repository
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class RepositoryIT {

  /**
   * Name of the collection without documents.
   */
  private static final String EMPTY_COLLECTION = "EMPTY_COLLECTION";

  /**
   * Name of the collection where add or remove document by the tests.
   */
  private static final String TEST_COLLECTION = "TEST_COLLECTION";

  /**
   * Name of the collection with 10 {@link DummyModel}.
   */
  private static final String TEN_DUMMY_COLLECTION = "TEN_DUMMY_COLLECTION";

  /**
   * The pool to use on the test.
   */
  protected static MongoClient pool;

  /**
   * Start the database.
   *
   * @param vertx       event bus to use.
   * @param testContext context for the test.
   */
  @BeforeAll
  public static void startDatabaseContainer(final Vertx vertx, final VertxTestContext testContext) {

    final var persitenceConf = Containers.status().startMongoContainer().getMongoDBConfig();
    pool = MongoClient.createShared(vertx, persitenceConf, "repository_pool_name");
    pool.createCollection(EMPTY_COLLECTION, testContext.succeeding(empty -> {

      pool.createCollection(TEST_COLLECTION, testContext.succeeding(test -> {

        pool.createCollection(TEN_DUMMY_COLLECTION, testContext.succeeding(ten -> {

          Future<String> future = Future.succeededFuture();
          for (var i = 0; i < 10; i++) {

            final DummyModel model = new DummyModel();
            model.index = i;
            future = future.compose(stored -> pool.insert(TEN_DUMMY_COLLECTION, model.toJsonObject()));

          }
          testContext.assertComplete(future).onSuccess(stored -> testContext.completeNow());

        }));

      }));

    }));

  }

  /**
   * Should not found page because query is wrong.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotSearchPageObjectBecauseQueryIsNotValid(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final FindOptions options = new FindOptions();
    options.setLimit(1000);
    testContext.assertFailure(repository.searchPageObject(EMPTY_COLLECTION,
        new JsonObject().put("$undefinedAction", -1), options, "models", null))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should return an empty page if no documents on collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldSearchPageObjectIsEmpty(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final FindOptions options = new FindOptions();
    options.setLimit(1000);
    testContext.assertComplete(repository.searchPageObject(EMPTY_COLLECTION, new JsonObject(), options, "models", null))
        .onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.getInteger("offset")).isNotNull().isEqualTo(0);
          assertThat(page.getInteger("total")).isNotNull().isEqualTo(0);
          assertThat(page.containsKey("models")).isFalse();
          testContext.completeNow();

        }));

  }

  /**
   * Should return an empty page because the offset is greater than the total.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldSearchPageObjectIsEmptyWhenOffsetIsGreaterTotal(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final FindOptions options = new FindOptions();
    options.setSkip(10);
    options.setLimit(1000);
    testContext
        .assertComplete(repository.searchPageObject(TEN_DUMMY_COLLECTION, new JsonObject(), options, "models", null))
        .onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.getInteger("offset")).isNotNull().isEqualTo(10);
          assertThat(page.getInteger("total")).isNotNull().isEqualTo(10);
          assertThat(page.containsKey("models")).isFalse();
          testContext.completeNow();

        }));

  }

  /**
   * Should return a page of models.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldSearchPageObjectModels(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final FindOptions options = new FindOptions();
    options.setSkip(1);
    options.setLimit(3);
    testContext.assertComplete(repository.searchPageObject(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(0))), options, "models",
        model -> model.remove("_id"))).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.getInteger("offset")).isNotNull().isEqualTo(1);
          assertThat(page.getInteger("total")).isNotNull().isEqualTo(5);
          final var models = Model.fromJsonArray(page.getJsonArray("models"), DummyModel.class);
          assertThat(models).isNotNull().hasSize(3);
          for (int pos = 0; pos < 3; pos++) {

            final var element = models.get(pos);
            final var expected = new DummyModel();
            expected.index = 2 + pos * 2;
            assertThat(element).isNotNull().isEqualTo(expected);

          }
          testContext.completeNow();

        }));

  }

  /**
   * Should return a page of models.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldSearchPageObject(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final FindOptions options = new FindOptions();
    options.setSkip(3);
    options.setLimit(1);
    testContext.assertComplete(repository.searchPageObject(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(1))), options, "models",
        null)).onSuccess(page -> testContext.verify(() -> {

          assertThat(page).isNotNull();
          assertThat(page.getInteger("offset")).isNotNull().isEqualTo(3);
          assertThat(page.getInteger("total")).isNotNull().isEqualTo(5);
          final var models = page.getJsonArray("models");
          assertThat(models).isNotNull();
          assertThat(models.size()).isEqualTo(1);
          final var element = models.getJsonObject(0);
          assertThat(element.getString("_id")).isNotNull();
          assertThat(element.getInteger("index")).isNotNull().isEqualTo(7);
          testContext.completeNow();

        }));

  }

  /**
   * Should not delete document because the query is wrong.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotDeleteDocumentBecauseQueryIsNotValid(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext
        .assertFailure(repository.deleteOneDocument(EMPTY_COLLECTION, new JsonObject().put("$undefinedAction", -1)))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not delete document because the collection is empty.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotDeleteDocumentBecauseEmptyCollection(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.deleteOneDocument(EMPTY_COLLECTION, new JsonObject()))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not delete document because not found.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotDeleteDocumentBecauseNotFound(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.deleteOneDocument(TEN_DUMMY_COLLECTION, new JsonObject().put("index", -1)))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should delete document.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldDeleteDocument(final VertxTestContext testContext) {

    pool.insert(TEST_COLLECTION, new JsonObject().put("index", -1567), testContext.succeeding(id -> {

      final Repository repository = new Repository(pool, "schemaVersion");
      final var query = new JsonObject().put("index", -1567);
      testContext.assertComplete(
          repository.deleteOneDocument(TEST_COLLECTION, query).compose(deleted -> pool.find(TEST_COLLECTION, query)))
          .onSuccess(value -> testContext.verify(() -> {

            assertThat(value).isEmpty();
            testContext.completeNow();

          }));

    }));

  }

  /**
   * Should not update {@code null} document.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotUpdateNullDocument(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.updateOneDocument(TEN_DUMMY_COLLECTION, new JsonObject(), null))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not update document from an empty collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotUpdateDocumentFormEmptyCollection(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext
        .assertFailure(
            repository.updateOneDocument(EMPTY_COLLECTION, new JsonObject(), new JsonObject().put("index", 100)))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not update document because it can not be found.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotUpdateDocumentIfNotFound(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.updateOneDocument(TEN_DUMMY_COLLECTION, new JsonObject().put("index", -1),
        new JsonObject().put("index", 100))).onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not update document because query is not valid.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotUpdateDocumentBecauseQueryIsNotValid(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.updateOneDocument(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$undefinedAction", -1)), new JsonObject().putNull("index")))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should update document.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldUpdateDocument(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    pool.insert(TEST_COLLECTION, document, testContext.succeeding(id -> {

      final Repository repository = new Repository(pool, "schemaVersion");
      final var query = new JsonObject().put("_id", id);
      testContext.assertComplete(repository
          .updateOneDocument(TEST_COLLECTION, query,
              new JsonObject().put("index", 100).putNull("key1").put("key3", true))
          .compose(updated -> pool.find(TEST_COLLECTION, query))).onSuccess(value -> testContext.verify(() -> {

            assertThat(value).isNotEmpty().hasSize(1);
            final var element = value.get(0);
            assertThat(element.getInteger("index")).isEqualTo(100);
            assertThat(element.containsKey("key1")).isFalse();
            assertThat(element.getInteger("key2")).isEqualTo(2);
            assertThat(element.getBoolean("key3")).isTrue();
            assertThat(element.getJsonArray("key4")).isEqualTo(new JsonArray());
            testContext.completeNow();

          }));
    }));

  }

  /**
   * Should not store with defined id.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotStoreWithDefinedId(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    testContext.assertComplete(pool.insert(TEST_COLLECTION, document).onSuccess(id -> {

      final Repository repository = new Repository(pool, "schemaVersion");
      document.put("_id", id);
      testContext.assertFailure(repository.storeOneDocument(TEST_COLLECTION, document, null))
          .onFailure(error -> testContext.completeNow());

    }));

  }

  /**
   * Should not store because map throws exception.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotStoreBecauseMapThrowsException(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    final Repository repository = new Repository(pool, "schemaVersion");
    testContext
        .assertFailure(repository.storeOneDocument(TEST_COLLECTION, document,
            model -> model.put("undefined", model.getString("undefined").toString())))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should store with a map.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldStoreWithMap(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    final var version = UUID.randomUUID().toString();
    final Repository repository = new Repository(pool, version);
    testContext.assertComplete(repository
        .storeOneDocument(TEST_COLLECTION, document, model -> model.put("id", model.remove("_id"))).compose(stored -> {

          document.mergeIn(stored);
          return pool.find(TEST_COLLECTION, new JsonObject().put(Repository.SCHEMA_VERSION, version));

        })).onSuccess(value -> testContext.verify(() -> {

          assertThat(value).isNotEmpty().hasSize(1);
          final var element = value.get(0);
          assertThat(element).isNotEqualTo(document);
          element.put("id", element.remove("_id"));
          assertThat(element).isNotEqualTo(document);
          element.remove(Repository.SCHEMA_VERSION);
          assertThat(element).isEqualTo(document);
          testContext.completeNow();

        }));

  }

  /**
   * Should store without a map.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldStoreWithoutMap(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    final var version = UUID.randomUUID().toString();
    final Repository repository = new Repository(pool, version);
    testContext.assertComplete(repository.storeOneDocument(TEST_COLLECTION, document, null).compose(stored -> {

      document.mergeIn(stored);
      return pool.find(TEST_COLLECTION, new JsonObject().put(Repository.SCHEMA_VERSION, version));

    })).onSuccess(value -> testContext.verify(() -> {

      assertThat(value).isNotEmpty().hasSize(1);
      final var element = value.get(0);
      assertThat(element).isNotEqualTo(document);
      element.remove(Repository.SCHEMA_VERSION);
      assertThat(element).isEqualTo(document);
      assertThat(element.getString("_id")).isNotNull();
      document.put("_id", element.getString("_id"));
      assertThat(element).isEqualTo(document);
      testContext.completeNow();

    }));

  }

  /**
   * Should not fond one document because the query is not valid.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotFindOneDocumentBecauseQueryNotValid(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext
        .assertFailure(repository.findOneDocument(TEN_DUMMY_COLLECTION,
            new JsonObject().put("index", new JsonObject().put("$undefinedAction", -1)), null, null))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not fond one document because any match query.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotFindOneDocumentBeauceAnyMatchQuery(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.findOneDocument(EMPTY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(0))),
        new JsonObject().put("index", false), null)).onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not fond one document because map fail.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotFindOneDocumentBeauceMapFails(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertFailure(repository.findOneDocument(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(0))),
        new JsonObject().put("index", false), model -> model.put("undefined", model.getString("undefined").toString())))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should fond one document.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldFindOneDocument(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertComplete(repository.findOneDocument(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(0))),
        new JsonObject().put("index", false), model -> {
          model.remove("_id");
          return model;
        })).onSuccess(found -> testContext.verify(() -> {

          assertThat(found).isEqualTo(new JsonObject());
          testContext.completeNow();

        }));

  }

  /**
   * Should fond one document without map.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldFindOneDocumentWithoutMap(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    testContext.assertComplete(repository.findOneDocument(TEN_DUMMY_COLLECTION,
        new JsonObject().put("index", new JsonObject().put("$mod", new JsonArray().add(2).add(0))),
        new JsonObject().put("index", false), null)).onSuccess(found -> testContext.verify(() -> {

          assertThat(found).isEqualTo(new JsonObject().put("_id", found.getString("_id")));
          testContext.completeNow();

        }));

  }

  /**
   * Should migrate empty collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldMigrateCollectionWhenItIsEmpty(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    repository.migrateCollection(EMPTY_COLLECTION, DummyModel.class)
        .onComplete(testContext.succeeding(migrated -> testContext.completeNow()));

  }

  /**
   * Should not migrate with bad query.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotMigrateCollectionWithBadQuery(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion") {

      /**
       * {@inheritDoc}
       */
      @Override
      protected JsonObject createQueryToReturnDocumentsWithAVersionLessThan(String version) {

        return new JsonObject().put(Repository.SCHEMA_VERSION, new JsonObject().put("$undefinedOperator", "value"));
      }

    };
    testContext.assertFailure(repository.migrateCollection(EMPTY_COLLECTION, DummyModel.class))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not migrate document with bad query.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotMigrateOneDocumentWithBadQuery(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final var query = new JsonObject().put(Repository.SCHEMA_VERSION,
        new JsonObject().put("$undefinedOperator", "value"));

    final long maxDocuments = 10l;
    testContext
        .assertFailure(repository.migrateOneDocument(TEN_DUMMY_COLLECTION, DummyModel.class, query, maxDocuments))
        .onComplete(error -> testContext.completeNow());

  }

  /**
   * Should not migrate document with empty collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotMigrateOneDocumentWithEmptyCollection(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final var query = new JsonObject().put(Repository.SCHEMA_VERSION,
        new JsonObject().put("$undefinedOperator", "value"));

    final long maxDocuments = 10l;
    testContext.assertFailure(repository.migrateOneDocument(EMPTY_COLLECTION, DummyModel.class, query, maxDocuments))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should not migrate document because a exception is thrown.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotMigrateOneDocumentBecauseExceptionIsThrown(final VertxTestContext testContext) {

    final Repository repository = new Repository(pool, "schemaVersion");
    final long maxDocuments = 10l;
    testContext.assertFailure(repository.migrateOneDocument(TEN_DUMMY_COLLECTION, null, new JsonObject(), maxDocuments))
        .onFailure(error -> testContext.completeNow());

  }

  /**
   * Should migrate one document.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldMigrateOneDocument(final VertxTestContext testContext) {

    final var document = new JsonObject().put("key1", "value").put("key2", 2).put("key3", false).put("key4",
        new JsonArray());
    pool.insert(TEST_COLLECTION, document, testContext.succeeding(id -> {

      final var version = UUID.randomUUID().toString();
      final Repository repository = new Repository(pool, version);
      testContext.assertComplete(testContext
          .assertComplete(
              repository.migrateOneDocument(TEST_COLLECTION, DummyModel.class, new JsonObject().put("_id", id), 1l))
          .compose(migrated -> pool.find(TEST_COLLECTION, new JsonObject().put("_id", id)))
          .onSuccess(value -> testContext.verify(() -> {

            assertThat(value).isNotEmpty().hasSize(1);
            final var element = value.get(0);
            final var expected = new JsonObject().put("_id", id).put(Repository.SCHEMA_VERSION, version).put("index",
                0);
            assertThat(element).isNotEqualTo(document).isEqualTo(expected);
            testContext.completeNow();

          })));

    }));

  }

  /**
   * Insert into a collection some {@link DummyComplexModel}.
   *
   * @param collectionName name of the collection to add the models.
   * @param size           the number of models to insert.
   * @param testContext    context for the test.
   * @param success        to call when the model has been inserted.
   */
  protected void createAndInsertDummyComplexModelsWithExtraFieldsTo(final String collectionName, final int size,
      final VertxTestContext testContext, final Runnable success) {

    if (size <= 0) {

      success.run();

    } else {

      final var model = new DummyComplexModelTest().createModelExample(size);
      model.id = null;
      final var document = model.toJsonObject().put("extraValue", "value")
          .put("extraObject", new JsonObject().put("key", "value"))
          .put("extraArray", new JsonArray().add(1).add(new JsonObject().put("key", "value")));
      if (model.siblings != null) {
        final var siblings = document.getJsonArray("siblings");
        for (var pos = 0; pos < siblings.size(); pos++) {

          siblings.getJsonObject(pos).put("extra", "value").put("extraObject", new JsonObject().put("key", "value"))
              .put("extraArray", new JsonArray().add(1).add(new JsonObject().put("key", "value")));

        }
      }
      pool.insert(collectionName, document, testContext.succeeding(stored -> this
          .createAndInsertDummyComplexModelsWithExtraFieldsTo(collectionName, size - 1, testContext, success)));
    }

  }

  /**
   * Should migrate collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldMigrateCollection(final VertxTestContext testContext) {

    final var collection = "_" + UUID.randomUUID().toString().replaceAll("-", "");
    pool.createCollection(collection, testContext.succeeding(createdCollections -> {

      this.createAndInsertDummyComplexModelsWithExtraFieldsTo(collection, 10, testContext, () -> {

        final var version = UUID.randomUUID().toString();
        final Repository repository = new Repository(pool, version);
        final FindOptions options = new FindOptions();
        options.setLimit(10000);
        options.getSort().put("index", 1);
        repository.migrateCollection(collection, DummyModel.class)
            .onComplete(testContext.succeeding(migrated -> pool.findWithOptions(collection, new JsonObject(), options,
                testContext.succeeding(values -> testContext.verify(() -> {

                  assertThat(values).isNotEmpty().hasSize(10);
                  final var expected = new JsonObject().put(Repository.SCHEMA_VERSION, version);
                  for (var i = 0; i < 10; i++) {

                    final var value = values.get(i);
                    expected.put("index", i + 1).put("_id", value.getString("_id"));
                    assertThat(value).isEqualTo(expected);
                  }

                  testContext.completeNow();

                })))));
      });
    }));

  }

  /**
   * Should migrate collection.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldMigrateCollectionWithDummyComplexModels(final VertxTestContext testContext) {

    final var collection = "_" + UUID.randomUUID().toString().replaceAll("-", "");
    pool.createCollection(collection, testContext.succeeding(createdCollections -> {

      this.createAndInsertDummyComplexModelsWithExtraFieldsTo(collection, 10, testContext, () -> {

        final var version = UUID.randomUUID().toString();
        final Repository repository = new Repository(pool, version);
        final FindOptions options = new FindOptions();
        options.setLimit(10000);
        options.getSort().put("index", 1);
        repository.migrateCollection(collection, DummyComplexModel.class)
            .onComplete(testContext.succeeding(migrated -> pool.findWithOptions(collection, new JsonObject(), options,
                testContext.succeeding(values -> testContext.verify(() -> {

                  assertThat(values).isNotEmpty().hasSize(10);
                  for (var i = 0; i < 10; i++) {

                    final var value = values.get(i);
                    final var expected = new DummyComplexModelTest().createModelExample(i + 1).toJsonObject();
                    expected.remove("id");
                    expected.put(Repository.SCHEMA_VERSION, version);
                    expected.put("_id", value.getString("_id"));
                    assertThat(value).isEqualTo(expected);
                  }

                  testContext.completeNow();

                })))));
      });
    }));

  }

  /**
   * Insert into a collection some {@link DummyModel} where add in some schema
   * version values.
   *
   * @param collectionName name of the collection to add the models.
   * @param size           the number of models to insert.
   * @param testContext    context for the test.
   * @param success        to call when the model has been inserted.
   */
  protected void createAndInsertDummyModelsWithSchemaVersionsTo(final String collectionName, final int size,
      final VertxTestContext testContext, final Runnable success) {

    if (size <= 0) {

      success.run();

    } else {

      final var model = new DummyModel();
      model.index = size;
      var document = model.toJsonObject();
      if (size % 2 == 0) {

        document = document.put(Repository.SCHEMA_VERSION, size);

      } else if (size % 3 == 0) {

        document = document.put(Repository.SCHEMA_VERSION, String.valueOf(size));

      } else if (size % 5 == 0) {

        document = document.put(Repository.SCHEMA_VERSION, new JsonArray().add(size).add("2"));

      } else if (size % 7 == 0) {

        document = document.put(Repository.SCHEMA_VERSION, new JsonObject().put("size", size));
      }
      pool.insert(collectionName, document, testContext.succeeding(stored -> this
          .createAndInsertDummyModelsWithSchemaVersionsTo(collectionName, size - 1, testContext, success)));
    }

  }

  /**
   * Should update the documents of a collection to the current schema version.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldUpdateSchemaVersionOnCollection(final VertxTestContext testContext) {

    final var collection = "_" + UUID.randomUUID().toString().replaceAll("-", "");
    pool.createCollection(collection, testContext.succeeding(createdCollections -> {

      this.createAndInsertDummyModelsWithSchemaVersionsTo(collection, 100, testContext, () -> {

        final var version = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
        final Repository repository = new Repository(pool, version);
        repository.updateSchemaVersionOnCollection(collection).onComplete(testContext.succeeding(empty -> {

          final FindOptions options = new FindOptions();
          options.setLimit(10000);
          options.getSort().put("index", 1);
          pool.findWithOptions(collection, new JsonObject(), options,
              testContext.succeeding(values -> testContext.verify(() -> {

                assertThat(values).isNotEmpty().hasSize(100);
                for (var i = 0; i < 100; i++) {

                  final var value = values.get(i);
                  final var expected = new JsonObject().put("_id", value.getString("_id")).put("index", i + 1)
                      .put(Repository.SCHEMA_VERSION, version);
                  assertThat(value).isEqualTo(expected);
                }

                testContext.completeNow();

              })));

        }));

      });

    }));

  }

  /**
   * Should not update the documents of a collection to the current schema
   * version.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldNotUpdateSchemaVersionOnCollection(final VertxTestContext testContext) {

    final var version = UUID.randomUUID().toString();

    final Repository repository = new Repository(pool, version) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected JsonObject createQueryToReturnDocumentsWithAVersionLessThan(String version) {

        return new JsonObject().put(Repository.SCHEMA_VERSION, new JsonObject().put("$undefinedOperator", "value"));
      }

    };
    repository.updateSchemaVersionOnCollection(TEN_DUMMY_COLLECTION).onComplete(testContext.failing(empty -> {

      final FindOptions options = new FindOptions();
      options.setLimit(10000);
      options.getSort().put("index", 1);
      pool.findWithOptions(TEN_DUMMY_COLLECTION, new JsonObject(), options,
          testContext.succeeding(values -> testContext.verify(() -> {

            assertThat(values).isNotEmpty().hasSize(10);
            for (var i = 0; i < 10; i++) {

              final var value = values.get(i);
              final var expected = new JsonObject().put("_id", value.getString("_id")).put("index", i);
              assertThat(value).isEqualTo(expected);
            }

            testContext.completeNow();

          })));

    }));

  }

  /**
   * Should store, find, update , find, delete and find a
   * {@link DummyComplexModel}.
   *
   * @param testContext context for the test.
   */
  @Test
  public void shouldStoreFindUpdateFindDeleteAndFindDummyComplexModel(final VertxTestContext testContext) {

    final var version = UUID.randomUUID().toString();
    final Repository repository = new Repository(pool, version);
    final var targetModel = new DummyComplexModelTest().createModelExample(1);
    targetModel.id = null;
    final Function<JsonObject, JsonObject> objectIdToModel = model -> model.put("id", model.remove("_id"));
    testContext
        .assertComplete(repository.storeOneDocument(TEST_COLLECTION, targetModel.toJsonObject(), objectIdToModel))
        .onSuccess(stored -> testContext.verify(() -> {

          final var storedModel = Model.fromJsonObject(stored, DummyComplexModel.class);
          assertThat(storedModel).isNotNull();
          final var id = storedModel.id;
          targetModel.id = id;
          assertThat(storedModel).isEqualTo(targetModel);
          final var byIdQuery = new JsonObject().put("_id", id);

          testContext.assertComplete(repository.findOneDocument(TEST_COLLECTION, byIdQuery, null, objectIdToModel))
              .onSuccess(found -> testContext.verify(() -> {

                final var foundModel = Model.fromJsonObject(found, DummyComplexModel.class);
                assertThat(foundModel).isEqualTo(storedModel);
                final var sourceModel = new DummyComplexModelTest().createModelExample(2);
                sourceModel.id = null;
                testContext
                    .assertComplete(
                        repository.updateOneDocument(TEST_COLLECTION, byIdQuery, sourceModel.toJsonObject()).compose(
                            updated -> repository.findOneDocument(TEST_COLLECTION, byIdQuery, null, objectIdToModel)))
                    .onSuccess(found2 -> testContext.verify(() -> {

                      final var foundModel2 = Model.fromJsonObject(found2, DummyComplexModel.class);
                      sourceModel.id = id;
                      assertThat(foundModel2).isEqualTo(sourceModel);

                      testContext.assertComplete(repository.deleteOneDocument(TEST_COLLECTION, byIdQuery))
                          .onSuccess(deleted -> {

                            testContext
                                .assertFailure(
                                    repository.findOneDocument(TEST_COLLECTION, byIdQuery, null, objectIdToModel))
                                .onFailure(error -> testContext.completeNow());

                          });

                    }));

              }));

        }));

  }

}
