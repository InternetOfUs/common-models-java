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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import eu.internetofus.common.model.ErrorMessageTest;
import eu.internetofus.common.model.Model;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import java.util.HashMap;
import java.util.UUID;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test the {@link ComponentClient}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class ComponentClientTest {

  /**
   * Verify that can not post a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.post(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not post a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.post(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not put a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.put(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not put a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPutJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.put(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.getJsonArray("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.getJsonObject("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that fail post when not return a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailPostIfNotReturnJsonObject(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      testContext.assertFailure(service.post(new JsonObject()).onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that fail get when not return a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailGetIfNotReturnJsonObject(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      testContext.assertFailure(service.getJsonObject().onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that fail get when not return a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailGetWithParamsIfNotReturnJsonObject(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      final var params = new HashMap<String, String>();
      params.put("key1", "1");
      params.put("key2", "2");
      params.put("key3", "3");
      testContext.assertFailure(service.getJsonObject(params).onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can get a JSON object.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldGetJsonObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "application/json");
      response.end("{\"key1\":1,\"key2\":\"2\"}");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      service.getJsonObject().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        server.close();
        assertThat(content).isEqualTo(new JsonObject().put("key1", 1).put("key2", "2"));
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can get a JSON object with params.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldGetJsonObjectWithParams(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "application/json");
      response.end("{\"key1\":1,\"key2\":\"2\"}");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      final var params = new HashMap<String, String>();
      params.put("key1", "1");
      params.put("key2", "2");
      params.put("key3", "3");
      service.getJsonObject(params).onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        server.close();
        assertThat(content).isEqualTo(new JsonObject().put("key1", 1).put("key2", "2"));
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that fail get when not return a JSON array.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailGetIfNotReturnJsonArray(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      testContext.assertFailure(service.getJsonArray().onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that fail get when not return a JSON array.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldFailGetWithParamsIfNotReturnJsonArray(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello World!");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      final var params = new HashMap<String, String>();
      params.put("key1", "1");
      params.put("key2", "2");
      params.put("key3", "3");
      testContext.assertFailure(service.getJsonArray(params).onFailure(cause -> testContext.verify(() -> {

        server.close();
        assertThat(cause).isInstanceOf(DecodeException.class);
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that get a JSON array.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldGetJsonArray(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "application/json");
      response.end("[1,2,3]");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      service.getJsonArray().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        server.close();
        assertThat(content).isEqualTo(new JsonArray().add(1).add(2).add(3));
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that get a JSON array.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldGetJsonArrayWithParams(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.putHeader("content-type", "application/json");
      response.end("[1,2,3]");
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      final var params = new HashMap<String, String>();
      params.put("key1", "1");
      params.put("key2", "2");
      params.put("key3", "3");
      service.getJsonArray(params).onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        server.close();
        assertThat(content).isEqualTo(new JsonArray().add(1).add(2).add(3));
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can not get over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotGetOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    testContext.assertFailure(service.getJsonObject("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not delete over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotDeleteOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined");
    testContext.assertFailure(service.delete("path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Test the creation of an absolute URL
   *
   * @param componentURL url to the component.
   * @param values       paths to set it are separated by :.
   * @param expectedURL  the URL that has to create the client.
   * @param vertx        event bus to use.
   * @param client       to use.
   * @param testContext  context that manage the test.
   */
  @ParameterizedTest(name = "Should the absolute URL for component {0} with parameters {1} be equals to {2}")
  @CsvSource({ "https://localhost:8080,,https://localhost:8080",
      "https://localhost:8080,a:b:c,https://localhost:8080/a/b/c",
      "https://localhost:8080,a:b:c,https://localhost:8080/a/b/c",
      "https://localhost:8080/,/a/:/b:c/,https://localhost:8080/a/b/c/",
      "https://localhost:8080/,/a/:b/:/c,https://localhost:8080/a/b/c",
      "https://localhost:8080/,a/:/b/:/c,https://localhost:8080/a/b/c",
      "https://localhost:8080/,a/b:/c,https://localhost:8080/a/b/c",
      "https://localhost:8080/,/a/b/:/c,https://localhost:8080/a/b/c",
      "https://localhost:8080/,a/:/b/c:d,https://localhost:8080/a/b/c/d",
      "https://localhost:8080/,/a/:b / d /:/c,https://localhost:8080/a/b%20/%20d%20/c",
      "https://localhost:8080,/a/b/c/,https://localhost:8080/a/b/c/",
      "https://localhost:8080/,/a/b/c/,https://localhost:8080/a/b/c/" })
  public void shouldCreateAbsoluteUrlWith(final String componentURL, final String values, final String expectedURL,
      final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var componentClient = new ComponentClient(client, componentURL);
    var paths = new Object[0];
    if (values != null) {

      paths = values.split(":");
    }
    assertThat(componentClient.createAbsoluteUrlWith(paths)).isEqualTo(expectedURL);
    testContext.completeNow();
  }

  /**
   * Verify that can not patch a {@link JsonArray} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonArrayOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.patch(new JsonArray(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not patch a {@link JsonObject} over an undefined service.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPatchJsonObjectOverAnUndefinedService(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.patch(new JsonObject(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can obtain the APi URL.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#obtainApiUrl()
   */
  @Test
  public void shouldObtainApiUrl(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var apiURL = "http://localhost:67890/" + UUID.randomUUID();
    final var service = new ComponentClient(client, apiURL);
    service.obtainApiUrl().onComplete(testContext.succeeding(api -> testContext.verify(() -> {

      assertThat(api).isEqualTo(apiURL);
      testContext.completeNow();

    })));

  }

  /**
   * Obtain the service exception from the response.
   *
   * @param response to process.
   *
   * @see ComponentClient#toServiceException(io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldServiceExeption(@Mock final HttpResponse<Buffer> response) {

    final var service = new ComponentClient(null, null);
    final var code = 400;
    doReturn(code).when(response).statusCode();
    final var message = UUID.randomUUID().toString();
    doReturn(message).when(response).statusMessage();
    final var exception = service.toServiceException(response);
    assertThat(exception).isNotNull();
    assertThat(exception.failureCode()).isEqualTo(code);
    assertThat(exception.getMessage()).isEqualTo(message);

  }

  /**
   * Obtain the service exception from the response with an error.
   *
   * @param response to process.
   *
   * @see ComponentClient#toServiceException(io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldServiceExeptionFromError(@Mock final HttpResponse<Buffer> response) {

    final var service = new ComponentClient(null, null);
    final var error = new ErrorMessageTest().createModelExample(2);
    doReturn(error.toBuffer()).when(response).bodyAsBuffer();
    final var code = 400;
    doReturn(code).when(response).statusCode();
    final var message = UUID.randomUUID().toString();
    doReturn(message).when(response).statusMessage();
    final var exception = service.toServiceException(response);
    assertThat(exception).isNotNull();
    assertThat(exception.failureCode()).isEqualTo(code);
    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getDebugInfo()).isEqualTo(error.toJsonObject());

  }

  /**
   * Consumed a no content fails if response has content.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#toServiceException(io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldNoContentConsumerFails(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    final var code = 400;
    doReturn(code).when(response).statusCode();
    service.noContentConsumer("actionId").accept(testContext.failing(error -> testContext.completeNow()), response);

  }

  /**
   * Consumed a no content success because the response is empty.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#toServiceException(io.vertx.ext.web.client.HttpResponse)
   */
  @Test
  public void shouldNoContentConsumerSuccess(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    final var code = Status.NO_CONTENT.getStatusCode();
    doReturn(code).when(response).statusCode();
    service.noContentConsumer("actionId").accept(testContext.succeeding(empty -> testContext.completeNow()), response);

  }

  /**
   * Should create action identifier.
   *
   * @see ComponentClient#createActionId(io.vertx.core.http.HttpMethod, String,
   *      java.util.Map)
   */
  @Test
  public void shouldCreateActionId() {

    final var service = new ComponentClient(null, null);
    final var params = new HashMap<String, String>();
    params.put("key1", "1");
    params.put("key2", "2");
    params.put("key3", "3");
    final var actionId = service.createActionId(HttpMethod.GET, "http://localhost", params);
    assertThat(actionId).contains("GET", "http://localhost", "?" + params.toString());

  }

  /**
   * Create request with parameters.
   *
   * @param vertx  event bus to use.
   * @param client to use.
   *
   * @see ComponentClient#createRequestFor(HttpMethod, String, java.util.Map)
   */
  @Test
  public void shouldCreateRequestWithParams(final Vertx vertx, final WebClient client) {

    final var service = new ComponentClient(client, "http://localhost:8080");
    final var params = new HashMap<String, String>();
    params.put("key1", "1");
    params.put("key2", "2");
    params.put("key3", "3");
    final var request = service.createRequestFor(HttpMethod.GET, "http://localhost:8080", params);
    final var query = request.queryParams();
    assertThat(query.size()).isEqualTo(3);
    assertThat(query.get("key1")).isEqualTo("1");
    assertThat(query.get("key2")).isEqualTo("2");
    assertThat(query.get("key3")).isEqualTo("3");

  }

  /**
   * Check that create a handler that can manage a no content response.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#createHandlerThatExtractBodyFromSuccessResponse(java.util.function.Function,
   *      io.vertx.core.Promise, String)
   */
  @Test
  public void shouldSuccessWhenTheReponseDoesNotHaveContent(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    doReturn(Status.NO_CONTENT.getStatusCode()).when(response).statusCode();
    final Promise<Model> promise = Promise.promise();
    service.createHandlerThatExtractBodyFromSuccessResponse(null, promise, "actionId").handle(response);
    promise.future().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

      assertThat(content).isNull();
      testContext.completeNow();

    })));

  }

  /**
   * Check that create a handler that can manage a response with an error.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#createHandlerThatExtractBodyFromSuccessResponse(java.util.function.Function,
   *      io.vertx.core.Promise, String)
   */
  @Test
  public void shouldSuccessWhenTheReponseHasAnerror(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    final var code = Status.BAD_REQUEST.getStatusCode();
    doReturn(code).when(response).statusCode();
    final var error = new ErrorMessageTest().createModelExample(2);
    doReturn(error.toBuffer()).when(response).bodyAsBuffer();
    final var message = UUID.randomUUID().toString();
    doReturn(message).when(response).statusMessage();

    final Promise<Model> promise = Promise.promise();
    service.createHandlerThatExtractBodyFromSuccessResponse(null, promise, "actionId").handle(response);
    promise.future().onComplete(testContext.failing(cause -> testContext.verify(() -> {

      assertThat(cause).isNotNull().isInstanceOf(ServiceException.class);
      final var exception = (ServiceException) cause;
      assertThat(exception.failureCode()).isEqualTo(code);
      assertThat(exception.getMessage()).isEqualTo(message);
      assertThat(exception.getDebugInfo()).isEqualTo(error.toJsonObject());

      testContext.completeNow();

    })));

  }

  /**
   * Check that create a handler that can manage success without content response.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#createHandlerWithAnyBodyAndSuccessResponse(io.vertx.core.Promise,
   *      String)
   */
  @Test
  public void shouldHandleWithoutBodyAndSuccessReponse(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    doReturn(Status.NO_CONTENT.getStatusCode()).when(response).statusCode();
    final Promise<Model> promise = Promise.promise();
    service.createHandlerWithAnyBodyAndSuccessResponse(promise, "actionId").handle(response);
    promise.future().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

      assertThat(content).isNull();
      testContext.completeNow();

    })));

  }

  /**
   * Check that create a handler that can manage success any content response.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#createHandlerWithAnyBodyAndSuccessResponse(io.vertx.core.Promise,
   *      String)
   */
  @Test
  public void shouldHandleWithBodyAndSuccessReponse(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    doReturn(Status.OK.getStatusCode()).when(response).statusCode();
    final Promise<Model> promise = Promise.promise();
    service.createHandlerWithAnyBodyAndSuccessResponse(promise, "actionId").handle(response);
    promise.future().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

      assertThat(content).isNull();
      testContext.completeNow();

    })));

  }

  /**
   * Check that create a handler that can manage any content with a response with
   * an error.
   *
   * @param response    to process.
   * @param testContext context that manage the test.
   *
   * @see ComponentClient#createHandlerWithAnyBodyAndSuccessResponse(io.vertx.core.Promise,
   *      String)
   */
  @Test
  public void shouldHandleWithBodyAndErrorReponse(@Mock final HttpResponse<Buffer> response,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(null, null);
    final var code = Status.BAD_REQUEST.getStatusCode();
    doReturn(code).when(response).statusCode();
    final var error = new ErrorMessageTest().createModelExample(2);
    doReturn(error.toBuffer()).when(response).bodyAsBuffer();
    final var message = UUID.randomUUID().toString();
    doReturn(message).when(response).statusMessage();

    final Promise<Model> promise = Promise.promise();
    service.createHandlerWithAnyBodyAndSuccessResponse(promise, "actionId").handle(response);
    promise.future().onComplete(testContext.failing(cause -> testContext.verify(() -> {

      assertThat(cause).isNotNull().isInstanceOf(ServiceException.class);
      final var exception = (ServiceException) cause;
      assertThat(exception.failureCode()).isEqualTo(code);
      assertThat(exception.getMessage()).isEqualTo(message);
      assertThat(exception.getDebugInfo()).isEqualTo(error.toJsonObject());

      testContext.completeNow();

    })));

  }

  /**
   * Verify that can not post with an extractor.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotPostWithExtractor(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.post(new JsonObject(), service.createVoidExtractor(), "path"))
        .onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can not delete with parameters.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotDeleteWithQueryParams(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.delete(new HashMap<>(), "path")).onFailure(ignored -> testContext.completeNow());

  }

  /**
   * Verify that can get a head response.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldHead(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.setStatusCode(204);
      response.putHeader("content-type", "application/json");
      response.end();
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      service.head().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        assertThat(content).isTrue();
        server.close();
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can get a head response.
   *
   * @param vertx       platform that manage the event bus.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldHeadFail(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    vertx.createHttpServer().requestHandler(request -> {
      final var response = request.response();
      response.setStatusCode(404);
      response.putHeader("content-type", "application/json");
      response.end();
    }).listen(0, "localhost", testContext.succeeding(server -> {

      final var service = new ComponentClient(client, "http://localhost:" + server.actualPort() + "/api");
      service.head().onComplete(testContext.succeeding(content -> testContext.verify(() -> {

        assertThat(content).isFalse();
        server.close();
        testContext.completeNow();

      })));

    }));

  }

  /**
   * Verify that can not get a head response.
   *
   * @param vertx       event bus to use.
   * @param client      to use.
   * @param testContext context that manage the test.
   */
  @Test
  public void shouldNotHead(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    final var service = new ComponentClient(client, "http://undefined/");
    testContext.assertFailure(service.head("path")).onFailure(ignored -> testContext.completeNow());

  }

}
