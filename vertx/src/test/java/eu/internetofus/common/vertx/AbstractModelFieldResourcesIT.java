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

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIsArrayOf;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

/**
 * Generic test over a resource that implements CRUD operation over a
 * {@link Model} field.
 *
 * @param <T>  type of model that contains the field.
 * @param <IT> type of the identifier for a model.
 * @param <E>  type of elements defined on the field.
 * @param <IE> type of the identifier for a field element.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractModelFieldResourcesIT<T extends Model, IT, E extends Model, IE> {

  /**
   * Return the path to the services to interact with the model.
   *
   * @return the path to the model.
   */
  protected abstract String modelPath();

  /**
   * Return the path to the services to interact with the model field.
   *
   * @return the path to the model field.
   */
  protected abstract String fieldPath();

  /**
   * Return the identifier of a model.
   *
   * @param model to obtain the identifier.
   *
   * @return the model identifier.
   */
  protected abstract IT idOfModel(T model);

  /**
   * Return the identifier of a model.
   *
   * @param model       to obtain the identifier.
   * @param testContext test context to use.
   *
   * @return the model identifier.
   */
  protected IT idOfModel(final T model, final VertxTestContext testContext) {

    final var id = this.idOfModel(model);
    if (id == null) {

      testContext.failNow("Can not obtain the identifier of the model " + model);

    }

    return id;
  }

  /**
   * Create a valid model that contains elements on the field and store it.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future stored model.
   */
  protected abstract Future<T> storeValidExampleModelWithFieldElements(int index, Vertx vertx,
      VertxTestContext testContext);

  /**
   * Return the path to an undefined identifier of the model.
   *
   * @return the path to a model with a bad identifier.
   */
  protected String undefinedModelIdPath() {

    return "/undefined-model-identifier";
  }

  /**
   * Create an example of an element field model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the future with the field element.
   */
  protected abstract Future<E> createValidModelFieldElementExample(final int index, final Vertx vertx,
      final VertxTestContext testContext);

  /**
   * Should not create an element if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelFieldElementExample(1, vertx, testContext)).onSuccess(source -> {

      final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath();
      testRequest(client, HttpMethod.POST, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(source.toJsonObject(), testContext);

    });
  }

  /**
   * Create a {@link JsonObject} that can not be converted to a {@link Model}
   * field element to test.
   *
   * @return a {@link JsonObject} that not represents a model field element.
   */
  protected JsonObject createBadJsonObjectModelFieldElement() {

    return new JsonObject().put("undefinedField", "value");
  }

  /**
   * Should not create an element if the JSON object is not valid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementWithBadJsonObject(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(1, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
      testRequest(client, HttpMethod.POST, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    });

  }

  /**
   * Create a model field element that is not valid.
   *
   * @return a model field element that is not valid.
   */
  protected abstract E createInvalidModelFieldElement();

  /**
   * Should not create an element if the element is not valid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementWithInvalidOne(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(1, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
      testRequest(client, HttpMethod.POST, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);

    });
  }

  /**
   * Return the field defined on a model.
   *
   * @param model to get the field.
   *
   * @return the field defined on the model.
   */
  protected abstract List<E> fieldOf(T model);

  /**
   * Return the field defined on a model.
   *
   * @param model       to obtain the identifier.
   * @param testContext test context to use.
   *
   * @return the field defined on the model.
   */
  protected List<E> fieldOf(final T model, final VertxTestContext testContext) {

    final var field = this.fieldOf(model);
    if (field == null) {

      testContext.failNow("Can not obtain the identifier of the model " + model);

    }

    return field;
  }

  /**
   * Should not create an element that is already defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementWithExistingOne(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(1, vertx, testContext)).onSuccess(model -> {

      final var element = this.fieldOf(model, testContext).get(0);
      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
      testRequest(client, HttpMethod.POST, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    });
  }

  /**
   * Create a valid model where the field is {@code null} and store it.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future the stored model.
   */
  protected abstract Future<T> storeValidExampleModelWithNullField(int index, Vertx vertx,
      VertxTestContext testContext);

  /**
   * Should create an element over a {@code null} field.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldCreateElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(1, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(100, vertx, testContext))
          .onSuccess(element -> {

            final var checkpoint = testContext.checkpoint(2);
            final var postPath = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
            testRequest(client, HttpMethod.POST, postPath).expect(res -> {

              assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
              @SuppressWarnings("unchecked")
              final var created = (E) assertThatBodyIs(element.getClass(), res);
              this.assertEqualsAdded(element, created);

              final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
              testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

                assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
                @SuppressWarnings("unchecked")
                final var updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
                final var updatedField = this.fieldOf(updatedModel);
                assertThat(updatedField).isNotEmpty().contains(created);

              }).sendJson(element.toJsonObject(), testContext, checkpoint);

            }).sendJson(element.toJsonObject(), testContext, checkpoint);

          });

    });

  }

  /**
   * Check if the added model is equals to the original.
   *
   * @param source model that is try to add.
   * @param target the added model.
   */
  protected abstract void assertEqualsAdded(E source, E target);

  /**
   * Should create an element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldCreateElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(2, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(200, vertx, testContext))
          .onSuccess(element -> {

            final var checkpoint = testContext.checkpoint(2);
            final var postPath = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
            testRequest(client, HttpMethod.POST, postPath).expect(res -> {

              assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
              @SuppressWarnings("unchecked")
              final var created = (E) assertThatBodyIs(element.getClass(), res);
              this.assertEqualsAdded(element, created);

              final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
              testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

                assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
                @SuppressWarnings("unchecked")
                final var updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
                final var updatedField = this.fieldOf(updatedModel);
                assertThat(updatedField).isNotEmpty().contains(created);

              }).sendJson(element.toJsonObject(), testContext, checkpoint);

            }).sendJson(element.toJsonObject(), testContext, checkpoint);

          });

    });

  }

  /**
   * Should not retrieve field over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveFieldOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath();
    testRequest(client, HttpMethod.GET, path).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Should retrieve empty list if field is {@code null}
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldRetrieveEmptyFieldIfItIsNullOnModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(1, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
      testRequest(client, HttpMethod.GET, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final List<E> field = assertThatBodyIsArrayOf(this.fieldElementClass(), res);
        assertThat(field).isNotNull().isEmpty();

      }).send(testContext);

    });
  }

  /**
   * Obtain the element class.
   *
   * @return the class of the elements on the field.
   */
  @SuppressWarnings("unchecked")
  protected Class<E> fieldElementClass() {

    return (Class<E>) this.createInvalidModelFieldElement().getClass();
  }

  /**
   * Should retrieve field form a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldRetrieveField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(3, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath();
      testRequest(client, HttpMethod.GET, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final List<E> field = assertThatBodyIsArrayOf(this.fieldElementClass(), res);
        assertThat(field).isNotNull().isEqualTo(this.fieldOf(model, testContext));

      }).send(testContext);

    });
  }

  /**
   * Should not retrieve element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath();
    testRequest(client, HttpMethod.GET, path).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Return the path to an undefined identifier of an element.
   *
   * @return the path to an element with a bad identifier.
   */
  protected String undefinedElementIdPath() {

    return "/-1";
  }

  /**
   * Should not retrieve element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverUndefinedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.GET, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    });

  }

  /**
   * Should not retrieve element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(4, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.GET, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    });

  }

  /**
   * Return the identifier of an element.
   *
   * @param model   where the element is defined.
   * @param element to obtain the identifier.
   *
   * @return the element identifier.
   */
  protected abstract IE idOfElementIn(T model, E element);

  /**
   * Should not retrieve element if it is deleted.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverDeletedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model, testContext);
      final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
      final var deletePath = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.DELETE, deletePath).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        final var getPath = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
        testRequest(client, HttpMethod.GET, getPath).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext, checkpoint);

      }).send(testContext, checkpoint);

    });

  }

  /**
   * Should retrieve element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldRetrieveElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.GET, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var retrievedElement = assertThatBodyIs(this.fieldElementClass(), res);
        assertThat(retrievedElement).isNotNull().isEqualTo(element);

      }).send(testContext);

    });

  }

  /**
   * Should not delete element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteElementOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath();
    testRequest(client, HttpMethod.DELETE, path).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Should not delete element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteElementOverUndefinedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.DELETE, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    });

  }

  /**
   * Should not delete element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(4, vertx, testContext)).onSuccess(model -> {

      final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.DELETE, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    });

  }

  /**
   * Should delete element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldDeleteElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var checkpoint = testContext.checkpoint(2);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.DELETE, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.DELETE, path).expect(res2 -> {

          assertThat(res2.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res2);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext, checkpoint);

      }).send(testContext, checkpoint);

    });

  }

  /**
   * Should not update element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

      final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    });

  }

  /**
   * Should not update element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverUndefinedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

        final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
            + this.undefinedElementIdPath();
        testRequest(client, HttpMethod.PUT, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Should not update element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

        final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
            + this.undefinedElementIdPath();
        testRequest(client, HttpMethod.PUT, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Should not update element if it is deleted.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverDeletedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.DELETE, path).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.PUT, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }).send(testContext, checkpoint);

    });

  }

  /**
   * Should not update with a bad JSON element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithBadJsonElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    });

  }

  /**
   * Should not update with an invalid element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithInvalidElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.PUT, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);

    });

  }

  /**
   * Should update element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldUpdateElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(11, vertx, testContext))
          .onSuccess(element -> {
            final var modelId = this.idOfModel(model, testContext);
            final var field = this.fieldOf(model, testContext);
            final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
            final var checkpoint = testContext.checkpoint(2);
            final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
            testRequest(client, HttpMethod.PUT, path).expect(res -> {

              assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
              @SuppressWarnings("unchecked")
              final var updated = (E) assertThatBodyIs(element.getClass(), res);
              this.assertEqualsAdded(element, updated);
              final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
              testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

                assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
                @SuppressWarnings("unchecked")
                final var updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
                final var updatedField = this.fieldOf(updatedModel);
                assertThat(updatedField).isNotEmpty().contains(updated);

              }).sendJson(element.toJsonObject(), testContext, checkpoint);

            }).sendJson(element.toJsonObject(), testContext, checkpoint);

          });

    });
  }

  /**
   * Should not merge element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

      final var path = this.modelPath() + this.undefinedModelIdPath() + this.fieldPath()
          + this.undefinedElementIdPath();
      testRequest(client, HttpMethod.PATCH, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    });

  }

  /**
   * Should not merge element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverUndefinedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

        final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
            + this.undefinedElementIdPath();
        testRequest(client, HttpMethod.PATCH, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Should not merge element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverNullField(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithNullField(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(4, vertx, testContext)).onSuccess(element -> {

        final var path = this.modelPath() + "/" + this.idOfModel(model, testContext) + this.fieldPath()
            + this.undefinedElementIdPath();
        testRequest(client, HttpMethod.PATCH, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Should not merge element if it is deleted.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverDeletedElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.DELETE, path).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.PATCH, path).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }).send(testContext, checkpoint);

    });

  }

  /**
   * Should not merge with a bad JSON element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithBadJsonElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.PATCH, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    });

  }

  /**
   * Should not merge with an invalid element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithInvalidElement(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      final var modelId = this.idOfModel(model, testContext);
      final var field = this.fieldOf(model, testContext);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
      testRequest(client, HttpMethod.PATCH, path).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);
    });

  }

  /**
   * Should merge element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldMergeElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.storeValidExampleModelWithFieldElements(4, vertx, testContext)).onSuccess(model -> {

      testContext.assertComplete(this.createValidModelFieldElementExample(11, vertx, testContext))
          .onSuccess(element -> {
            final var modelId = this.idOfModel(model, testContext);
            final var field = this.fieldOf(model, testContext);
            final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
            final var checkpoint = testContext.checkpoint(2);
            final var path = this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId;
            testRequest(client, HttpMethod.PATCH, path).expect(res -> {

              assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
              @SuppressWarnings("unchecked")
              final var merged = (E) assertThatBodyIs(element.getClass(), res);
              this.assertEqualsAdded(element, merged);
              final var getPath = this.modelPath() + "/" + this.idOfModel(model, testContext);
              testRequest(client, HttpMethod.GET, getPath).expect(resRetrieve -> {

                assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
                @SuppressWarnings("unchecked")
                final var mergedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
                final var mergedField = this.fieldOf(mergedModel);
                assertThat(mergedField).isNotEmpty().contains(merged);

              }).sendJson(element.toJsonObject(), testContext, checkpoint);

            }).sendJson(element.toJsonObject(), testContext, checkpoint);

          });

    });
  }
}
