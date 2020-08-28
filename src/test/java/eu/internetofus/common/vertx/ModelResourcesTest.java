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
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.internetofus.common.components.DummyComplexModel;
import eu.internetofus.common.components.DummyComplexModelTest;
import eu.internetofus.common.components.DummyModel;
import eu.internetofus.common.components.DummyModelTest;
import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

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
   * Should not retrieve if no found model.
   *
   * @param searcher      the function that will search the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   *
   * @see ModelResources#retrieveModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldNotRetrieveModelIfNotFound(@Mock final BiConsumer<String, Handler<AsyncResult<DummyModel>>> searcher, @Mock final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var context = new OperationRequest();
    ModelResources.retrieveModel(searcher, "id", "modelName", context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * @see ModelResources#retrieveModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldNotRetrieveModelIfFoundModelIsNull(@Mock final BiConsumer<String, Handler<AsyncResult<DummyModel>>> searcher, @Mock final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var context = new OperationRequest();
    ModelResources.retrieveModel(searcher, "id", "modelName", context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    searchHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * @see ModelResources#retrieveModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldRetrieveModel(@Mock final BiConsumer<String, Handler<AsyncResult<DummyModel>>> searcher, @Mock final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var context = new OperationRequest();
    ModelResources.retrieveModel(searcher, "id", "modelName", context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyModel>>> searchHandler = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), searchHandler.capture());
    final var expectedModel = new DummyModelTest().createModelExample(2);
    searchHandler.getValue().handle(Future.succeededFuture(expectedModel));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    final var model = Model.fromBuffer(result.getPayload(), DummyModel.class);
    assertThat(model).isNotNull().isEqualTo(expectedModel);

  }

  /**
   * Should not delete if no found model.
   *
   * @param deleter       the function that will delete the model.
   * @param resultHandler handler to manage the HTTP result.
   *
   * @see ModelResources#deleteModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldNotDeleteModelIfNotFound(@Mock final BiConsumer<String, Handler<AsyncResult<Void>>> deleter, @Mock final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var context = new OperationRequest();
    ModelResources.deleteModel(deleter, "id", "modelName", context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> deleteHandler = ArgumentCaptor.forClass(Handler.class);
    verify(deleter, timeout(30000).times(1)).accept(any(), deleteHandler.capture());
    deleteHandler.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * @see ModelResources#deleteModel(java.util.function.BiConsumer, String, String, io.vertx.ext.web.api.OperationRequest,
   *      io.vertx.core.Handler)
   */
  @Test
  public void shouldDeleteModel(@Mock final BiConsumer<String, Handler<AsyncResult<Void>>> deleter, @Mock final Handler<AsyncResult<OperationResponse>> resultHandler) {

    final var context = new OperationRequest();
    ModelResources.deleteModel(deleter, "id", "modelName", context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<Void>>> deleteHandler = ArgumentCaptor.forClass(Handler.class);
    verify(deleter, timeout(30000).times(1)).accept(any(), deleteHandler.capture());
    deleteHandler.getValue().handle(Future.succeededFuture());

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    assertThat(result.getPayload()).isNull();

  }

  /**
   * Should not validate a model that not match the type.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       function to call when the validation success.
   *
   * @see ModelResources#deleteModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldNotValidateIfModelNotMatchType(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final Consumer<DummyComplexModel> success) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    ModelResources.validate(vertx, DummyComplexModel.class, new JsonObject().put("undefinedKey", "value"), "modelName", context, resultHandler, success);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * Should not validate an invalid model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       function to call when the validation success.
   *
   * @see ModelResources#deleteModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldNotValidateIfModelIsNotValid(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final Consumer<DummyComplexModel> success) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    ModelResources.validate(vertx, DummyComplexModel.class, new JsonObject(), "modelName", context, resultHandler, success);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("'id'");

  }

  /**
   * Should validate a model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param success       function to call when the validation success.
   *
   * @see ModelResources#deleteModel(BiConsumer, String, String, OperationRequest, Handler)
   */
  @Test
  public void shouldValidateModel(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final Consumer<DummyComplexModel> success) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    final var expected = new DummyComplexModelTest().createModelExample(2);
    ModelResources.validate(vertx, DummyComplexModel.class, expected.toJsonObject(), "modelName", context, resultHandler, success);
    verify(success, timeout(30000).times(1)).accept(expected);

  }

  /**
   * Should not create a model if it is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param storer        the function used to store the model.
   *
   * @see ModelResources#createModel(Vertx, Class, JsonObject, String, BiConsumer, OperationRequest, Handler)
   */
  @Test
  public void shouldNotCreateModelBecauseItIsNotNotValid(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    ModelResources.createModel(vertx, DummyComplexModel.class, new JsonObject(), "modelName", storer, context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("'id'");

  }

  /**
   * Should not create a model if it is not valid.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param storer        the function used to store the model.
   *
   * @see ModelResources#createModel(Vertx, Class, JsonObject, String, BiConsumer, OperationRequest, Handler)
   */
  @Test
  public void shouldNotCreateModelBecauseCanNotBeStored(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    final var expected = new DummyComplexModelTest().createModelExample(1);
    ModelResources.createModel(vertx, DummyComplexModel.class, expected.toJsonObject(), "modelName", storer, context, resultHandler);

    final var cause = new Throwable("Can not store the model");
    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(storer, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.failedFuture(cause));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * @see ModelResources#createModel(Vertx, Class, JsonObject, String, BiConsumer, OperationRequest, Handler)
   */
  @Test
  public void shouldCreateModel(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<DummyComplexModel>>> storer) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    final var expected = new DummyComplexModelTest().createModelExample(1);
    ModelResources.createModel(vertx, DummyComplexModel.class, expected.toJsonObject(), "modelName", storer, context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(storer, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var stored = new DummyComplexModelTest().createModelExample(2);
    storeCaptor.getValue().handle(Future.succeededFuture(stored));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
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
   * @see ModelResources#mergeModel(Vertx, String, String, Class, BiConsumer, JsonObject, BiConsumer, OperationRequest,
   *      Handler, MergeConsumer)
   */
  @Test
  public void shouldNotMergeModelBecauseNoMatchType(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    ModelResources.mergeModel(vertx, "id", "modelName", DummyComplexModel.class, searcher, new JsonObject().put("undefined_key", "value"), updater, context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName");
    assertThat(error.message).contains("'id'");

  }

  /**
   * Should not merge a value because can not found the target model.
   *
   * @param resultHandler handler to manage the HTTP result.
   * @param searcher      the function used to search a model.
   * @param updater       the function used to update a model.
   *
   * @see ModelResources#mergeModel(Vertx, String, String, Class, BiConsumer, JsonObject, BiConsumer, OperationRequest,
   *      Handler, MergeConsumer)
   */
  @Test
  public void shouldNotMergeModelBecauseNotFound(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    ModelResources.mergeModel(vertx, "id", "modelName", DummyComplexModel.class, searcher, new DummyComplexModelTest().createModelExample(1).toJsonObject(), updater, context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    storeCaptor.getValue().handle(Future.failedFuture("Not found"));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
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
   * @see ModelResources#mergeModel(Vertx, String, String, Class, BiConsumer, JsonObject, BiConsumer, OperationRequest,
   *      Handler, MergeConsumer)
   */
  @Test
  public void shouldNotMergeModelBecauseNotValid(@Mock final Handler<AsyncResult<OperationResponse>> resultHandler, @Mock final BiConsumer<String, Handler<AsyncResult<DummyComplexModel>>> searcher,
      @Mock final BiConsumer<DummyComplexModel, Handler<AsyncResult<Void>>> updater) {

    final var context = new OperationRequest();
    final var vertx = Vertx.vertx();
    final var source = new DummyComplexModelTest().createModelExample(1);
    source.id = ValidationsTest.STRING_256;
    ModelResources.mergeModel(vertx, "id", "modelName", DummyComplexModel.class, searcher, source.toJsonObject(), updater, context, resultHandler);

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Handler<AsyncResult<DummyComplexModel>>> storeCaptor = ArgumentCaptor.forClass(Handler.class);
    verify(searcher, timeout(30000).times(1)).accept(any(), storeCaptor.capture());
    final var target = new DummyComplexModelTest().createModelExample(1);
    storeCaptor.getValue().handle(Future.succeededFuture(target));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<AsyncResult<OperationResponse>> resultCaptor = ArgumentCaptor.forClass(AsyncResult.class);
    verify(resultHandler, timeout(30000).times(1)).handle(resultCaptor.capture());
    final var asyncResult = resultCaptor.getValue();
    assertThat(asyncResult.failed()).isFalse();
    final var result = asyncResult.result();
    assertThat(result.getStatusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    final var error = Model.fromBuffer(result.getPayload(), ErrorMessage.class);
    assertThat(error).isNotNull();
    assertThat(error.code).contains("modelName.id");
    assertThat(error.message).contains("id");

  }

}
