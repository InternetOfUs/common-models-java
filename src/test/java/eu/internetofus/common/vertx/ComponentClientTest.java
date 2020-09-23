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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import eu.internetofus.common.components.DummyModel;
import eu.internetofus.common.components.DummyModelTest;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.profile_manager.CreateUpdateTsDetails;
import eu.internetofus.common.components.profile_manager.WeNetUserProfile;
import eu.internetofus.common.components.profile_manager.WeNetUserProfileTest;
import eu.internetofus.common.components.task_manager.Task;
import eu.internetofus.common.components.task_manager.TaskType;
import eu.internetofus.common.components.task_manager.TaskTypeTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.impl.HttpResponseImpl;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.junit5.web.VertxWebClientExtension;
import io.vertx.serviceproxy.ServiceException;

/**
 * Test the {@link ComponentClient}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
@ExtendWith(VertxWebClientExtension.class)
public class ComponentClientTest {

  /**
   * Verify that can not post a {@link Model} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostModelOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.post(new DummyModel(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not post a {@link JsonArray} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonArrayOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.post(new JsonArray(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not post a {@link JsonObject} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonObjectOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.post(new JsonObject(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not put a {@link Model} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutModelOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.put(new DummyModel(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not put a {@link JsonArray} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonArrayOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.put(new JsonArray(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not put a {@link JsonObject} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonObjectOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.put(new JsonObject(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not get a {@link Model} from undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetModelOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.getModel(DummyModel.class, testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonArrayOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.getJsonArray(testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonObjectOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.getJsonObject(testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that fail post when not return a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailPostIfNotReturnJsonObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      service.post(new JsonObject(), testContext.failing(cause -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      }));

    }));

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    service.getJsonObject(testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not delete over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotDeleteOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    service.delete(testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that if the handler fail the model handler fails too.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shoulHandlerForModelFail(final WebClient client, final VertxTestContext testContext) {

    final var cause = new Throwable("Cause");
    ComponentClient.handlerForModel(CreateUpdateTsDetails.class, testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isSameAs(cause);
      testContext.completeNow();

    }))).handle(Future.failedFuture(cause));

  }

  /**
   * Verify that if no model is succeeding the handler do not receive a model.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shoulHandlerForModelSuccessWithoutResult(final WebClient client, final VertxTestContext testContext) {

    ComponentClient.handlerForModel(CreateUpdateTsDetails.class, testContext.succeeding(result -> testContext.verify(() -> {

      assertThat(result).isNull();
      testContext.completeNow();

    }))).handle(Future.succeededFuture());

  }

  /**
   * Verify that the handler receive the model defined in the JSON.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shoulHandlerForModelSuccessWithModel(final WebClient client, final VertxTestContext testContext) {

    final var model = new WeNetUserProfileTest().createBasicExample(1);
    ComponentClient.handlerForModel(WeNetUserProfile.class, testContext.succeeding(result -> testContext.verify(() -> {

      assertThat(result).isEqualTo(model);
      testContext.completeNow();

    }))).handle(Future.succeededFuture(model.toJsonObject()));

  }

  /**
   * Verify that if the return JSON not match the type the handler receives an error.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shoulHandlerForModelFailBecauseJsonNotMatchType(final WebClient client, final VertxTestContext testContext) {

    final var jsonModel = new JsonObject().put("id", new JsonObject().put("key", "value"));
    ComponentClient.handlerForModel(WeNetUserProfile.class, testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).hasMessageContaining("WeNetUserProfile").hasMessageContaining(jsonModel.toString());
      testContext.completeNow();

    }))).handle(Future.succeededFuture(jsonModel));

  }

  /**
   * Test the creation of an absolute URL
   *
   * @param componentURL url to the component.
   * @param values       paths to set it are separated by :.
   * @param expectedURL  the URL that has to create the client.
   * @param client       to use.
   * @param testContext  context that manage the test.
   */
  @ParameterizedTest(name = "Should the absolute URL for component {0} with parameters {1} be equals to {2}")
  @CsvSource({ "https://localhost:8080,     ,https://localhost:8080", "https://localhost:8080,a:b:c,https://localhost:8080/a/b/c", "https://localhost:8080,   a  :  b  : c  ,https://localhost:8080/a/b/c",
    "https://localhost:8080/,/a/:/b:c/,https://localhost:8080/a/b/c/", "https://localhost:8080/,  /a/  :  b/  :  /c  ,https://localhost:8080/a/b/c", "https://localhost:8080/,  a/  :  /b/  :  /c  ,https://localhost:8080/a/b/c" })
  public void shouldCreateAbsoluteUrlWith(final String componentURL, final String values, final String expectedURL, final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, componentURL);
    var paths = new Object[0];
    if (values != null) {

      paths = values.split(":");
    }
    assertThat(componentClient.createAbsoluteUrlWith(paths)).isEqualTo(expectedURL);
    testContext.completeNow();
  }

  /**
   * Should notify error when the response does not have an error message.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldNotifyWhenTheReposeDoesNotContainsErrorMessage(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 404, "statusMessage", null, null, null, null, null);
    componentClient.notifyErrorTo(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(ServiceException.class);
      final var serviceException = (ServiceException) exception;
      assertThat(serviceException.failureCode()).isEqualTo(404);
      assertThat(serviceException.getMessage()).isEqualTo("statusMessage");
      testContext.completeNow();

    })), response);
  }

  /**
   * Should fail when consumer an array from an object response.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeArrayWithObjectReponse(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 404, "statusMessage", null, null, null, new JsonObject().toBuffer(), null);
    componentClient.jsonArrayConsumer("actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(DecodeException.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should fail when consumer an array that the response return as null.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeArrayWithReturnNullArray(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 404, "statusMessage", null, null, null, new JsonObject().toBuffer(), null) {
      /**
       * {@inheritDoc}
       */
      @Override
      public JsonArray bodyAsJsonArray() {

        return null;
      }
    };
    componentClient.jsonArrayConsumer("actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(NoStackTraceThrowable.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should fail when consumer an object from an array response.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeObjectWithArrayReponse(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 404, "statusMessage", null, null, null, new JsonArray().toBuffer(), null);
    componentClient.jsonObjectConsumer("actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(DecodeException.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should fail when consumer an object that the response return as null.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeObjectWithReturnNullObject(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 404, "statusMessage", null, null, null, new JsonObject().toBuffer(), null) {
      /**
       * {@inheritDoc}
       */
      @Override
      public JsonObject bodyAsJsonObject() {

        return null;
      }
    };
    componentClient.jsonObjectConsumer("actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(NoStackTraceThrowable.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should fail when consumer no context and the response has content.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeNoContentWithContent(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 200, "statusMessage", null, null, null, new JsonArray().toBuffer(), null);
    componentClient.noContentConsumer("actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(NoStackTraceThrowable.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should fail when consumer a model when the content contain other model.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldFailConsumeModelWithUnexpectedModelReponse(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 200, "statusMessage", null, null, null, new TaskTypeTest().createModelExample(1).toBuffer(), null);
    componentClient.modelConsumer(Task.class, "actionId").accept(testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isInstanceOf(NoStackTraceThrowable.class);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should consume a model.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#notifyErrorTo(io.vertx.core.Handler, io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldConsumeModel(final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, "http://localhost:8080");
    final var model = new TaskTypeTest().createModelExample(1);
    final HttpResponse<Buffer> response = new HttpResponseImpl<Buffer>(HttpVersion.HTTP_1_1, 200, "statusMessage", null, null, null, model.toBuffer(), null);
    componentClient.modelConsumer(TaskType.class, "actionId").accept(testContext.succeeding(consumed -> testContext.verify(() -> {

      assertThat(consumed).isEqualTo(model);
      testContext.completeNow();

    })), response);

  }

  /**
   * Should provide the same error when handle for list fails.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldHandleForListModelWhenHandleFail(final WebClient client, final VertxTestContext testContext) {

    final var cause = new Throwable("Cause" + UUID.randomUUID());
    ComponentClient.handlerForListModel(DummyModel.class, testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception).isSameAs(cause);
      testContext.completeNow();

    }))).handle(Future.failedFuture(cause));
  }

  /**
   * Should provide success empty when {@code null} result.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldHandleForListModelWhenHandleReturnNull(final WebClient client, final VertxTestContext testContext) {

    ComponentClient.handlerForListModel(DummyModel.class, testContext.succeeding(result -> testContext.verify(() -> {

      assertThat(result).isNull();
      testContext.completeNow();

    }))).handle(Future.succeededFuture());
  }

  /**
   * Should provide success empty when {@code empty} result.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldHandleForListModelWhenHandleReturnEmpty(final WebClient client, final VertxTestContext testContext) {

    ComponentClient.handlerForListModel(DummyModel.class, testContext.succeeding(result -> testContext.verify(() -> {

      assertThat(result).isEqualTo(Collections.emptyList());
      testContext.completeNow();

    }))).handle(Future.succeededFuture(new JsonArray()));
  }

  /**
   * Should provide success empty when {@code empty} result.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldFailHandleForListModelWhenReturnArrayDoesNotHaveValidModel(final WebClient client, final VertxTestContext testContext) {

    final var badModel = new JsonObject().put("undefinedKey", "value");
    ComponentClient.handlerForListModel(DummyModel.class, testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception.getMessage()).contains(badModel.toString(), "1", DummyModel.class.toString());
      testContext.completeNow();

    }))).handle(Future.succeededFuture(new JsonArray().add(new JsonObject()).add(badModel)));
  }

  /**
   * Should provide success empty when {@code empty} result.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldFailHandleForListModelWhenReturnArrayDoesNotHaveJsonObjects(final WebClient client, final VertxTestContext testContext) {

    ComponentClient.handlerForListModel(DummyModel.class, testContext.failing(exception -> testContext.verify(() -> {

      assertThat(exception.getMessage()).contains("1", "0", DummyModel.class.toString());
      testContext.completeNow();

    }))).handle(Future.succeededFuture(new JsonArray().add(1)));
  }

  /**
   * Should provide success empty when {@code empty} result.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#handlerForListModel(Class, io.vertx.core.Handler)
   */
  @Test
  public void shouldHandleForListModelWithAnArrayOdModels(final WebClient client, final VertxTestContext testContext) {

    var source = new JsonArray();
    final List<DummyModel> expected = new ArrayList<>();
    for (var i = 0; i < 10; i++) {

      final var model = new DummyModelTest().createModelExample(i);
      expected.add(model);
      source = source.add(model.toJsonObject());

    }

    ComponentClient.handlerForListModel(DummyModel.class, testContext.succeeding(result -> testContext.verify(() -> {

      assertThat(result).isEqualTo(expected);
      testContext.completeNow();

    }))).handle(Future.succeededFuture(source));
  }

  /**
   * Verify that can not patch a {@link Model} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchModelOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.patch(new DummyModel(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not patch a {@link JsonArray} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonArrayOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.patch(new JsonArray(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

  /**
   * Verify that can not patch a {@link JsonObject} over an undefined service.
   *
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonObjectOverAnUndefinedService(final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    service.patch(new JsonObject(), testContext.failing(ignored -> testContext.completeNow()), "path");

  }

}
