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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.internetofus.common.components.DummyComplexModel;
import eu.internetofus.common.components.DummyComplexModelTest;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;

/**
 * Test the {@link ModelResources}
 *
 * @see ModelResources
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(MockitoExtension.class)
public class ModelResourcesTest {

  /**
   * Create new model context.
   *
   * @return the create context.
   */
  protected ModelContext<DummyComplexModel, String> createModelContext() {

    final var model = new ModelContext<DummyComplexModel, String>();
    model.id = "id";
    model.name = "modelName";
    model.type = DummyComplexModel.class;
    return model;
  }

  /**
   * Create a new operation context.
   *
   * @param resultHandler handler to manage the HTTP result.
   *
   * @return the created context.
   */
  protected ServiceContext createServiceContext(final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    return new ServiceContext(new ServiceRequest(), resultHandler);

  }

  /**
   * Should not retrieve if no found model.
   *
   * @param searcher      the function that will search the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#retrieveModel(ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelIfNotFound(@Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher, @Mock final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModel(model, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");

  }

  /**
   * Should not retrieve if the found model is {@code null}.
   *
   * @param searcher      the function that will search the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#retrieveModel(ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelIfFoundModelIsNull(@Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher, @Mock final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModel(model, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");

  }

  /**
   * Should retrieve a model.
   *
   * @param searcher      the function that will search the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#retrieveModel(ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldRetrieveModel(@Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher, @Mock final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModel(model, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var expectedModel = new DummyComplexModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(expectedModel));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isEqualTo(expectedModel);

  }

  /**
   * Should not delete if no found model.
   *
   * @param deleter       the function that will delete the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#deleteModel(ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotDeleteModelIfNotFound(@Mock final BiConsumer<String, Handler<AsyncResult<Void>>> deleter, @Mock final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModel(model, deleter, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> deleteHandler = ArgumentCaptor.forClass(Handler.class);
    verify(deleter, timeout(30000).times(1)).accept(any(), deleteHandler.capture());
    deleteHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");

  }

  /**
   * Should delete a model.
   *
   * @param deleter       the function that will delete the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#deleteModel(ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldDeleteModel(@Mock final BiConsumer<String, Handler<AsyncResult<Void>>> deleter, @Mock final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModel(model, deleter, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> deleteHandler = ArgumentCaptor.forClass(Handler.class);
    verify(deleter, timeout(30000).times(1)).accept(any(), deleteHandler.capture());
    deleteHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    assertThat(result.getPayload()).isNull();

  }

  /**
   * Should not validate an invalid model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       function to call when the validation success.
   *
   * @see ModelResources#validate(Vertx, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldNotValidateIfModelIsNotValid(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final Runnable success) {

    final var model = this.createModelContext();
    model.source = new DummyComplexModelTest().createModelExample(2);
    model.source.id = ValidationsTest.STRING_256;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.validate(vertx, model, context, success);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName.id");
    assertThat(error.message).contains("too large");

  }

  /**
   * Should validate a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       function to call when the validation success.
   *
   * @see ModelResources#validate(Vertx, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldValidateModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final Runnable success) {

    final var model = this.createModelContext();
    final var expected = new DummyComplexModelTest().createModelExample(2);
    model.source = expected;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();

    ModelResources.validate(vertx, model, context, success);
    verify(success, timeout(30000).times(1)).run();
    assertThat(model.value).isEqualTo(expected);

  }

  /**
   * Should not create a model if it is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param storer        the function used to store the model.
   *
   * @see ModelResources#createModel(Vertx, JsonObject, ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelBecauseItIsNotNotValid(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var model = this.createModelContext();
    final var expected = new DummyComplexModelTest().createModelExample(2);
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.createModel(vertx, expected.toJsonObject().put("id", ValidationsTest.STRING_256), model, storer, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("too large");

  }

  /**
   * Should not create a model if it is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param storer        the function used to store the model.
   *
   * @see ModelResources#createModel(Vertx, JsonObject, ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelBecauseCanNotBeStored(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var model = this.createModelContext();
    final var expected = new DummyComplexModelTest().createModelExample(2);
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.createModel(vertx, expected.toJsonObject(), model, storer, context);

    final var cause = new Throwable("Can not store the model");
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(storer, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.failedFuture(cause));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.message).contains(cause.getMessage());

  }

  /**
   * Should create a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param storer        the function used to store the model.
   *
   * @see ModelResources#createModel(Vertx, JsonObject, ModelContext, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldCreateModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var model = this.createModelContext();
    final var expected = new DummyComplexModelTest().createModelExample(1);
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.createModel(vertx, expected.toJsonObject(), model, storer, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(storer, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var stored = new DummyComplexModelTest().createModelExample(2);
    storeCaptor.getValue().handle(Future.succeededFuture(stored));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.CREATED.getStatusCode());
    final var created = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(created).isNotNull().isEqualTo(stored);

  }

  /**
   * Should not merge a value that is not of the model type.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelBecauseNoMatchType(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.mergeModel(vertx, new JsonObject().put("undefined_key", "value"), model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName");

  }

  /**
   * Should not merge a value because can not found the target model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelBecauseNotFound(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var value = new DummyComplexModelTest().createModelExample(1).toJsonObject();
    ModelResources.mergeModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("'id'");

  }

  /**
   * Should not merge a value because the merged value is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelBecauseNotValid(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = ValidationsTest.STRING_256;
    final var value = source.toJsonObject();
    ModelResources.mergeModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName.siblings[0].id");
    assertThat(error.message).contains("too large");

  }

  /**
   * Should not merge a value because the merged value is equals to the original.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelBecauseNoChanges(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.mergeModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.succeededFuture(source));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isEqualTo("modelName_to_merge_equal_to_original");
    assertThat(error.message).contains("modelName");

  }

  /**
   * Should not merge a value because the update fails.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelBecauseUpdateFails(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.mergeModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(updater, timeout(30000).times(1)).accept(any(), updateCaptor.capture());
    updateCaptor.getValue().handle(Future.failedFuture("Can not be updated"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);

  }

  /**
   * Should merge a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldMergeModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.mergeModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(updater, timeout(30000).times(1)).accept(any(), updateCaptor.capture());
    updateCaptor.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var merged = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(merged).isNotNull().isNotEqualTo(source).isNotEqualTo(target);
    target.index = source.index;
    target.siblings = source.siblings;
    assertThat(merged).isEqualTo(target);

  }

  /**
   * Should not update a value that is not of the model type.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelBecauseNoMatchType(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    ModelResources.updateModel(vertx, new JsonObject().put("undefined_key", "value"), model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName");

  }

  /**
   * Should not update a value because can not found the target model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelBecauseNotFound(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var value = new DummyComplexModelTest().createModelExample(1).toJsonObject();
    ModelResources.updateModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("'id'");

  }

  /**
   * Should not update a value because the updated value is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelBecauseNotValid(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    source.siblings = new ArrayList<>();
    source.siblings.add(new DummyComplexModel());
    source.siblings.get(0).id = ValidationsTest.STRING_256;
    final var value = source.toJsonObject();
    ModelResources.updateModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName.siblings[0].id");
    assertThat(error.message).contains("too large");

  }

  /**
   * Should not update a value because the updated value is equals to the original.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelBecauseNoChanges(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.updateModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.succeededFuture(source));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isEqualTo("modelName_to_update_equal_to_original");
    assertThat(error.message).contains("modelName");

  }

  /**
   * Should not update a value because the update fails.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelBecauseUpdateFails(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.updateModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(updater, timeout(30000).times(1)).accept(any(), updateCaptor.capture());
    updateCaptor.getValue().handle(Future.failedFuture("Can not be updated"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);

  }

  /**
   * Should update a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#updateModel(Vertx, JsonObject, ModelContext, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldUpdateModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(2);
    final var value = source.toJsonObject();
    ModelResources.updateModel(vertx, value, model, searcher, updater, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> updateCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(updater, timeout(30000).times(1)).accept(any(), updateCaptor.capture());
    updateCaptor.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var updated = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(updated).isNotNull().isNotEqualTo(source).isNotEqualTo(target);
    target.index = source.index;
    target.siblings = source.siblings;
    assertThat(updated).isEqualTo(target);

  }

  /**
   * Should not retrieve the model field because it can not found the model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelField(ModelContext, BiConsumer, java.util.function.Function, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelFieldBecauseNotFouncModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelField(model, searcher, dummy -> dummy.siblings, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should retrieve empty model if the field is {@code null}.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelField(ModelContext, BiConsumer, java.util.function.Function, ServiceContext)
   */
  @Test
  public void shouldRetrieveEmptyModelField(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelField(model, searcher, dummy -> dummy.siblings, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new DummyComplexModel()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromJsonArray(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isEqualTo(Collections.emptyList());

  }

  /**
   * Should retrieve model field value.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelField(ModelContext, BiConsumer, java.util.function.Function, ServiceContext)
   */
  @Test
  public void shouldRetrieveModelField(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelField(model, searcher, dummy -> dummy.siblings, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var target = new DummyComplexModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromJsonArray(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isEqualTo(target.siblings);

  }

  /**
   * Create new model context.
   *
   * @return the create context.
   */
  protected ModelFieldContext<DummyComplexModel, String, DummyComplexModel, String> createModelFieldContextById() {

    final var element = new ModelFieldContext<DummyComplexModel, String, DummyComplexModel, String>();
    element.id = "id";
    element.name = "siblings";
    element.type = DummyComplexModel.class;
    element.model = this.createModelContext();
    return element;
  }

  /**
   * Should not retrieve the model field element because it can not found the model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelFieldElement(ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelFieldElementBecauseNotFoundModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should not retrieve the model field element because the field is {@code null}.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelFieldElement(ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelFieldElementBecauseFieldIsNull(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new DummyComplexModel()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName_siblings");
    assertThat(error.message).contains("modelName", "id", "siblings");
  }

  /**
   * Should not retrieve the model field element because the element not found.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelFieldElement(ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, ServiceContext)
   */
  @Test
  public void shouldNotRetrieveModelFieldElementBecauseElementNotFound(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(new DummyComplexModelTest().createModelExample(2)));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName_siblings");
    assertThat(error.message).contains("modelName", "id", "siblings");
  }

  /**
   * Should retrieve model field element.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   *
   * @see ModelResources#retrieveModelFieldElement(ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, ServiceContext)
   */
  @Test
  public void shouldRetrieveModelFieldElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.id = target.siblings.get(1).id;
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isEqualTo(target.siblings.get(1));
  }

  /**
   * Should not update the model field element because the value to update is not right.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelFieldElementBecauseBadValue(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var valueToUpdate = new JsonObject().put("undefined", "value");
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("siblings");
  }

  /**
   * Should not update the model field element because it can not found the model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelFieldElementBecauseNotFoundModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    final var valueToUpdate = source.toJsonObject();
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should not update the model field element because it can not found the element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelFieldElementBecauseNotFoundElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    final var valueToUpdate = source.toJsonObject();
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var target = new DummyComplexModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName", "siblings");
    assertThat(error.message).contains("modelName", "id", "siblings");
  }

  /**
   * Should not update the model field element because it can not update the element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelFieldElementBecauseCanNotUpdateElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = target.siblings.get(1);
    element.id = source.id;
    final var valueToUpdate = source.toJsonObject();
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("siblings");
  }

  /**
   * Should not update the model field element because it can not stored updated model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotUpdateModelFieldElementBecauseCanNotStoreUpdated(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = new DummyComplexModel();
    element.id = target.siblings.get(1).id;
    final var valueToUpdate = source.toJsonObject();
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerUpdateModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.failedFuture("Can not store"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);
    assertThat(error.message).isEqualTo("Can not store");
  }

  /**
   * Should update model field element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerUpdateModel the function to update the model.
   *
   * @see ModelResources#updateModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldUpdateModelFieldElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerUpdateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = new DummyComplexModel();
    element.id = target.siblings.get(1).id;
    final var valueToUpdate = source.toJsonObject();
    ModelResources.updateModelFieldElement(vertx, valueToUpdate, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerUpdateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerUpdateModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isNotEqualTo(source).isNotEqualTo(target).isNotEqualTo(target.siblings.get(1));
    target.siblings.get(1).index = 0;
    assertThat(retrieveModel).isEqualTo(target.siblings.get(1));
  }

  /**
   * Should not merge the model field element because the value to merge is not right.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelFieldElementBecauseBadValue(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var valueToMerge = new JsonObject().put("undefined", "value");
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("siblings");
  }

  /**
   * Should not merge the model field element because it can not found the model.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelFieldElementBecauseNotFoundModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    final var valueToMerge = source.toJsonObject();
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should not merge the model field element because it can not found the element.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelFieldElementBecauseNotFoundElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    final var valueToMerge = source.toJsonObject();
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var target = new DummyComplexModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName", "siblings");
    assertThat(error.message).contains("modelName", "id", "siblings");
  }

  /**
   * Should not merge the model field element because it can not merge the element.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelFieldElementBecauseCanNotMergeElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = target.siblings.get(1);
    element.id = source.id;
    final var valueToMerge = source.toJsonObject();
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("siblings");
  }

  /**
   * Should not merge the model field element because it can not stored merged model.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotMergeModelFieldElementBecauseCanNotStoreMerged(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = new DummyComplexModel();
    element.id = target.siblings.get(1).id;
    final var valueToMerge = source.toJsonObject();
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerMergeModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.failedFuture("Can not store"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);
    assertThat(error.message).isEqualTo("Can not store");
  }

  /**
   * Should merge model field element.
   *
   * @param resultHandler    handler to manage the HTTP result.
   * @param searcher         the function used to search a model.
   * @param storerMergeModel the function to merge the model.
   *
   * @see ModelResources#mergeModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldMergeModelFieldElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerMergeModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var target = new DummyComplexModelTest().createModelExample(2);
    final var source = new DummyComplexModel();
    element.id = target.siblings.get(1).id;
    final var valueToMerge = source.toJsonObject();
    ModelResources.mergeModelFieldElement(vertx, valueToMerge, element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerMergeModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerMergeModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isNotEqualTo(source).isNotEqualTo(target).isNotEqualTo(target.siblings.get(1));
    target.siblings.get(1).index = 0;
    assertThat(retrieveModel).isEqualTo(target.siblings.get(1));
  }

  /**
   * Should not delete the model field element because it can not found the model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerDeleteModel the function to delete the model.
   *
   * @see ModelResources#deleteModelFieldElement( ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotDeleteModelFieldElementBecauseNotFoundModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerDeleteModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerDeleteModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should not delete the model field element because it can not found the element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerDeleteModel the function to delete the model.
   *
   * @see ModelResources#deleteModelFieldElement( ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotDeleteModelFieldElementBecauseNotFoundElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerDeleteModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerDeleteModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var target = new DummyComplexModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName", "siblings");
    assertThat(error.message).contains("modelName", "id", "siblings");
  }

  /**
   * Should not delete the model field element because it can not stored deleted model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerDeleteModel the function to delete the model.
   *
   * @see ModelResources#deleteModelFieldElement( ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotDeleteModelFieldElementBecauseCanNotStoreDeleted(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerDeleteModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.id = target.siblings.get(0).id;
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerDeleteModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerDeleteModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.failedFuture("Can not store"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);
    assertThat(error.message).isEqualTo("Can not store");
  }

  /**
   * Should delete model field element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerDeleteModel the function to delete the model.
   *
   * @see ModelResources#deleteModelFieldElement( ModelFieldContext, BiConsumer, java.util.function.Function,
   *      java.util.function.BiFunction, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldDeleteModelFieldElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerDeleteModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.id = target.siblings.get(0).id;
    final var context = this.createServiceContext(resultHandler);
    ModelResources.deleteModelFieldElement(element, searcher, dummy -> dummy.siblings, ModelResources.searchElementById((dummy, id) -> id != null && id.equals(dummy.id)), storerDeleteModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerDeleteModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    assertThat(element.model.value).isNotNull().isNotEqualTo(target);
    assertThat(element.model.value.siblings).isNotEqualTo(target.siblings).hasSize(1).contains(target.siblings.get(1)).doesNotContain(target.siblings.get(0));
  }

  /**
   * Should not create the model field element because the value to create is not right.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelFieldElementBecauseBadValue(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var valueToCreate = new JsonObject().put("undefined", "value");
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("siblings");
  }

  /**
   * Should not create the model field element because it can not found the model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelFieldElementBecauseNotFoundModel(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    final var valueToCreate = source.toJsonObject();
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("modelName", "id");
  }

  /**
   * Should not create the model field element because the element to add is not valid.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelFieldElementBecauseElementIsNotValid(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.model.id = target.id;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(3);
    source.id = ValidationsTest.STRING_256;
    final var valueToCreate = source.toJsonObject();
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("siblings");
    assertThat(error.message).contains("too large");
  }

  /**
   * Should not create the model field element because it can not stored created model.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldNotCreateModelFieldElementBecauseCanNotStoreCreated(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.model.id = target.id;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModel();
    final var valueToCreate = source.toJsonObject();
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerCreateModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.failedFuture("Can not store"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotEqualTo(error.message);
    assertThat(error.message).isEqualTo("Can not store");
  }

  /**
   * Should create model field element.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldCreateModelFieldElement(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(2);
    element.model.id = target.id;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModel();
    final var valueToCreate = source.toJsonObject();
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerCreateModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isNotEqualTo(source).isNotEqualTo(target);
    source.id = retrieveModel.id;
    assertThat(retrieveModel).isEqualTo(source);
    target.siblings.add(source);
    assertThat(element.model.value).isEqualTo(target);
  }

  /**
   * Should create model field element when field is {@code null}.
   *
   * @param resultHandler     handler to manage the HTTP result.
   * @param searcher          the function used to search a model.
   * @param storerCreateModel the function to create the model.
   *
   * @see ModelResources#createModelFieldElement(Vertx, JsonObject, ModelFieldContext, BiConsumer,
   *      java.util.function.Function, BiConsumer, BiConsumer, ServiceContext)
   */
  @Test
  public void shouldCreateModelFieldElementWehnFieldIsNull(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> storerCreateModel) {

    final var element = this.createModelFieldContextById();
    final var target = new DummyComplexModelTest().createModelExample(1);
    element.model.id = target.id;
    final var context = this.createServiceContext(resultHandler);
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModel();
    final var valueToCreate = source.toJsonObject();
    ModelResources.createModelFieldElement(vertx, valueToCreate, element, searcher, dummy -> dummy.siblings, (dummy, siblings) -> dummy.siblings = siblings, storerCreateModel, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> storerHandler = ArgumentCaptor.forClass(Handler.class);
    verify(storerCreateModel, timeout(30000).times(1)).accept(any(), storerHandler.capture());
    storerHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var retrieveModel = Model.fromBuffer(result.getPayload(), DummyComplexModel.class);
    assertThat(retrieveModel).isNotNull().isNotEqualTo(source);
    source.id = retrieveModel.id;
    assertThat(retrieveModel).isEqualTo(source);
    target.siblings = new ArrayList<>();
    target.siblings.add(source);
    assertThat(element.model.value).isEqualTo(target);
  }

  /**
   * Check not found element if the list is {@code null}.
   */
  @Test
  public void shoulNotFoundElementWithIdForNullList() {

    assertThat(ModelResources.searchElementById((dummy, id) -> true).apply(null, "1")).isEqualTo(-1);

  }

  /**
   * Check not found element if the list is empty.
   */
  @Test
  public void shoulNotFoundElementWithIdForEmptyList() {

    final var models = new ArrayList<>();
    assertThat(ModelResources.searchElementById((dummy, id) -> true).apply(models, "1")).isEqualTo(-1);

  }

  /**
   * Check not found element if any element has the id.
   */
  @Test
  public void shoulNotFoundElementWithIdForUndefinedId() {

    final var models = new ArrayList<DummyComplexModel>();
    models.add(new DummyComplexModelTest().createModelExample(1));
    models.add(new DummyComplexModelTest().createModelExample(2));
    final BiPredicate<DummyComplexModel, String> idComparator = (dummy, id) -> id != null && id.equals(dummy.id);
    assertThat(ModelResources.searchElementById(idComparator).apply(models, "undefinedId")).isEqualTo(-1);

  }

  /**
   * Check found element by id.
   */
  @Test
  public void shouldFoundElementWithId() {

    final var models = new ArrayList<DummyComplexModel>();
    final BiPredicate<DummyComplexModel, String> idComparator = (dummy, id) -> id != null && id.equals(dummy.id);
    for (var i = 0; i < 10; i++) {

      models.add(new DummyComplexModelTest().createModelExample(i));
      assertThat(ModelResources.searchElementById(idComparator).apply(models, models.get(i).id)).isEqualTo(i);
    }

  }

  /**
   * Check not found element if the list is {@code null}.
   */
  @Test
  public void shoulNotFoundElementWithIndexForNullList() {

    assertThat(ModelResources.searchElementByIndex().apply(null, 1)).isEqualTo(-1);

  }

  /**
   * Check not found element if the list is empty.
   */
  @Test
  public void shoulNotFoundElementWithIndexForEmptyList() {

    final var models = new ArrayList<>();
    assertThat(ModelResources.searchElementByIndex().apply(models, 1)).isEqualTo(-1);

  }

  /**
   * Check not found element if they are not defined.
   */
  @Test
  public void shoulNotFoundElementWithIndexForUndefinedIndex() {

    final var models = new ArrayList<>();
    models.add(new DummyComplexModelTest().createModelExample(1));
    models.add(new DummyComplexModelTest().createModelExample(2));
    assertThat(ModelResources.searchElementByIndex().apply(models, null)).isEqualTo(-1);
    assertThat(ModelResources.searchElementByIndex().apply(models, -2)).isEqualTo(-1);
    assertThat(ModelResources.searchElementByIndex().apply(models, models.size())).isEqualTo(-1);

  }

  /**
   * Check found element by index.
   */
  @Test
  public void shouldFoundElementWithIndex() {

    final var models = new ArrayList<>();
    for (var i = 0; i < 10; i++) {

      models.add(new DummyComplexModelTest().createModelExample(i));
      assertThat(ModelResources.searchElementByIndex().apply(models, i)).isEqualTo(i);
    }

  }

  /**
   * Should not convert a {@code null} JSON object to a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       the function to call if the conversion was a success.
   *
   * @see ModelResources#toModel(JsonObject, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldNotConvertANullJsonObject(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final Runnable success) {

    final var model = this.createModelContext();
    final var context = this.createServiceContext(resultHandler);
    ModelResources.toModel(null, model, context, success);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains(model.name);
    assertThat(error.message).contains(model.name).isNotEqualTo(error.code);

    verify(success, never()).run();
  }

  /**
   * Should not obtain page if search throws exception.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function to obtain the page.
   *
   * @see ModelResources#toModel(JsonObject, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldNotRetrieveModelsPageWhenSearchThrowsException(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<ModelsPageContext, Promise<JsonObject>> searcher) {

    doThrow(new RuntimeException("Error")).when(searcher).accept(any(), any());
    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelsPage(0, 100, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotNull().isNotEqualTo(error.message);
    assertThat(error.message).isNotNull();
  }

  /**
   * Should not obtain page because the search has failed.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function to obtain the page.
   *
   * @see ModelResources#toModel(JsonObject, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldNotRetrieveModelsPageWhenSearchFailed(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<ModelsPageContext, Promise<JsonObject>> searcher) {

    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelsPage(0, 100, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Promise<JsonObject>> searchHandler = ArgumentCaptor.forClass(Promise.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().fail("Not found");

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).isNotNull().isNotEqualTo(error.message);
    assertThat(error.message).isNotNull();
  }

  /**
   * Should obtain models page.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function to obtain the page.
   *
   * @see ModelResources#toModel(JsonObject, ModelContext, ServiceContext, Runnable)
   */
  @Test
  public void shouldRetrieveModelsPage(@Mock final Handler<AsyncResult<ServiceResponse>> resultHandler, @Mock final BiConsumer<ModelsPageContext, Promise<JsonObject>> searcher) {

    final var context = this.createServiceContext(resultHandler);
    ModelResources.retrieveModelsPage(0, 100, searcher, context);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Promise<JsonObject>> searchHandler = ArgumentCaptor.forClass(Promise.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final JsonObject expectedPage = new JsonObject().put("models", new JsonArray()).put("offset", 0).put("limit", 100);
    searchHandler.getValue().complete(expectedPage);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<ServiceResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var page = Json.decodeValue(result.getPayload());
    assertThat(page).isNotNull().isEqualTo(expectedPage);
  }

}
