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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * General test over the classes that extends the {@link Repository}.
 *
 * @param <T> type of repository to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public abstract class RepositoryTestCase<T extends Repository> {

  /**
   * Create the repository to use in the tests.
   *
   * @param pool the mocked mongo client.
   *
   * @return the instance to use in the tests.
   */
  protected abstract T createRepository(MongoClient pool);

  /**
   * Check search communities fail because can not obtain the number of
   * communities that match.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchPageObjectFailedByMongoClientCount(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.searchPageObject(null, null, new FindOptions(), null, null, testContext.failing(search -> {
      testContext.completeNow();
    }));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Long>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).count(any(), any(), handler.capture());
    handler.getValue().handle(Future.failedFuture("Internal error"));

  }

  /**
   * Check search communities fail because can not find.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchPageObjectFailedByMongoClientFind(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.searchPageObject(null, null, new FindOptions(), null, null, testContext.failing(search -> {
      testContext.completeNow();
    }));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Long>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).count(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture(100L));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<List<JsonObject>>>> findHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).findWithOptions(any(), any(), any(), findHandler.capture());
    findHandler.getValue().handle(Future.failedFuture("Internal error"));

  }

  /**
   * Check search page return empty page because the model and map it.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchPageReturnEmptyWhenTotalIsZero(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    final FindOptions options = new FindOptions();
    final int offset = 23;
    options.setSkip(offset);
    repository.searchPageObject(null, null, options, "resultKey", null,
        testContext.succeeding(page -> testContext.verify(() -> {

          assertThat(page.getInteger("offset")).isEqualTo(offset);
          assertThat(page.getLong("total")).isEqualTo(0L);
          assertThat(page.containsKey("resultKey")).isFalse();
          testContext.completeNow();
        })));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Long>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).count(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture(0L));

  }

  /**
   * Check store model and map it.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchPage(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    final FindOptions options = new FindOptions();
    final int offset = 23;
    options.setSkip(offset);
    final long total = 25;
    final List<JsonObject> models = new ArrayList<>();
    models.add(new JsonObject().put("id", "1"));
    models.add(new JsonObject().put("id", "2"));
    repository.searchPageObject(null, null, options, "resultKey", null,
        testContext.succeeding(page -> testContext.verify(() -> {

          assertThat(page.getInteger("offset")).isEqualTo(offset);
          assertThat(page.getLong("total")).isEqualTo(total);
          assertThat(page.getJsonArray("resultKey")).isEqualTo(new JsonArray(models));
          testContext.completeNow();
        })));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Long>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).count(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture(total));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<List<JsonObject>>>> findHandler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).findWithOptions(any(), any(), any(), findHandler.capture());
    findHandler.getValue().handle(Future.succeededFuture(models));

  }

  /**
   * Check delete fail by mongo client.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldDeleteOneDocuemntFailedByMongoClient(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.deleteOneDocument(null, null, testContext.failing(delete -> {
      testContext.completeNow();
    }));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<MongoClientDeleteResult>>> handler = ArgumentCaptor
    .forClass(Handler.class);
    verify(pool, times(1)).removeDocument(any(), any(), handler.capture());
    handler.getValue().handle(Future.failedFuture("Internal error"));

  }

  /**
   * Check update fail by mongo client.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldUpdateOneDocumentFailedByMongoClient(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.updateOneDocument(null, null, null, testContext.failing(update -> {
      testContext.completeNow();
    }));

  }

  /**
   * Check store fail by mongo client.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldStoreOneDocumentFailedByMongoClient(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.storeOneDocument(null, null, null, testContext.failing(update -> {
      testContext.completeNow();
    }));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).insert(any(), any(), handler.capture());
    handler.getValue().handle(Future.failedFuture("Internal error"));

  }

  /**
   * Check search fail by mongo client.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldSearchOneDocumentFailedByMongoClient(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    repository.findOneDocument(null, null, null, null, testContext.failing(update -> {
      testContext.completeNow();
    }));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<JsonObject>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).findOne(any(), any(), any(), handler.capture());
    handler.getValue().handle(Future.failedFuture("Internal error"));

  }

  /**
   * Check store model without mapping it.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldStoreOneDocumentWithoutMappingIt(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    final JsonObject model = new JsonObject();
    repository.storeOneDocument(null, model, null, testContext.succeeding(stored -> testContext.verify(() -> {

      assertThat(stored).isSameAs(model);
      testContext.completeNow();
    })));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).insert(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture("id"));

  }

  /**
   * Check store model and map it.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldStoreOneDocumentAndMapResult(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    final JsonObject model = new JsonObject();
    repository.storeOneDocument(null, model, map -> map.put("id", "value"),
        testContext.succeeding(stored -> testContext.verify(() -> {

          assertThat(stored).isSameAs(model);
          assertThat(stored.getString("id")).isSameAs("value");
          testContext.completeNow();
        })));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).insert(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture("id"));

  }

  /**
   * Check store model and capture mapping exception.
   *
   * @param pool        mocked MongoDB client.
   * @param testContext test context.
   */
  @Test
  public void shouldStoreOneDocumentButCaptureMappingException(@Mock final MongoClient pool, final VertxTestContext testContext) {

    final T repository = this.createRepository(pool);
    final JsonObject model = new JsonObject();
    final RuntimeException error = new RuntimeException("Unexpected error");
    repository.storeOneDocument(null, model, map -> {
      throw error;
    }, testContext.failing(storeError -> testContext.verify(() -> {

      assertThat(storeError).isSameAs(error);
      testContext.completeNow();
    })));
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<String>>> handler = ArgumentCaptor.forClass(Handler.class);
    verify(pool, times(1)).insert(any(), any(), handler.capture());
    handler.getValue().handle(Future.succeededFuture("id"));

  }

}
