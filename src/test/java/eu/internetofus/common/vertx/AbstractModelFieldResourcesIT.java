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

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIsArrayOf;
import static eu.internetofus.common.vertx.ext.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;

/**
 * Generic test over a resource that implements CRUD operation over a {@link Model} field.
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
   * Create a valid model that contains elements on the field and store it.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   * @param succeeding  to inform of the stored model.
   */
  protected abstract void storeValidExampleModelWithFieldElements(int index, Vertx vertx, VertxTestContext testContext, Handler<AsyncResult<T>> succeeding);

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
   * @param index         to use in the example.
   * @param vertx         event bus to use.
   * @param testContext   test context to use.
   * @param createHandler the component that will manage the created model.
   */
  protected abstract void createValidModelFieldElementExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<E>> createHandler);

  /**
   * Should not create an element if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModelFieldElementExample(1, vertx, testContext, testContext.succeeding(source -> {

      testRequest(client, HttpMethod.POST, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(source.toJsonObject(), testContext);

    }));
  }

  /**
   * Create a {@link JsonObject} that can not be converted to a {@link Model} field element to test.
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
  public void shouldNotCreateElementWithBadJsonObject(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(1, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.POST, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    }));
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
  public void shouldNotCreateElementWithInvalidOne(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(1, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.POST, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);

    }));
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
   * Should not create an element that is already defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateElementWithExistingOne(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(1, vertx, testContext, testContext.succeeding(model -> {

      final var element = this.fieldOf(model).get(0);
      testRequest(client, HttpMethod.POST, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    }));
  }

  /**
   * Create a valid model where the field is {@code null} and store it.
   *
   * @param index       of the example to store.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   * @param succeeding  to inform of the stored model.
   */
  protected abstract void storeValidExampleModelWithNullField(int index, Vertx vertx, VertxTestContext testContext, Handler<AsyncResult<T>> succeeding);

  /**
   * Should create an element over a {@code null} field.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldCreateElementOverNullField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(1, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(100, vertx, testContext, testContext.succeeding(element -> {

        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.POST, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          @SuppressWarnings("unchecked")
          final E created = (E) assertThatBodyIs(element.getClass(), res);
          this.assertEqualsAdded(element, created);

          testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model)).expect(resRetrieve -> {

            assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
            final var updatedField = this.fieldOf(updatedModel);
            assertThat(updatedField).isNotEmpty().contains(created);

          }).sendJson(element.toJsonObject(), testContext, checkpoint);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }));

    }));

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

    this.storeValidExampleModelWithFieldElements(2, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(200, vertx, testContext, testContext.succeeding(element -> {

        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.POST, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          @SuppressWarnings("unchecked")
          final E created = (E) assertThatBodyIs(element.getClass(), res);
          this.assertEqualsAdded(element, created);

          testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model)).expect(resRetrieve -> {

            assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
            final var updatedField = this.fieldOf(updatedModel);
            assertThat(updatedField).isNotEmpty().contains(created);

          }).sendJson(element.toJsonObject(), testContext, checkpoint);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }));

    }));

  }

  /**
   * Should not retrieve field over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveFieldOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath()).expect(res -> {

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
  public void shouldRetrieveEmptyFieldIfItIsNullOnModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(1, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final List<E> field = assertThatBodyIsArrayOf(this.fieldElementClass(), res);
        assertThat(field).isNotNull().isEmpty();

      }).send(testContext);

    }));
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

    this.storeValidExampleModelWithFieldElements(3, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final List<E> field = assertThatBodyIsArrayOf(this.fieldElementClass(), res);
        assertThat(field).isNotNull().isEqualTo(this.fieldOf(model));

      }).send(testContext);

    }));
  }

  /**
   * Should not retrieve element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

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
  public void shouldNotRetrieveElementOverUndefinedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should not retrieve element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveElementOverNullField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(4, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

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
  public void shouldNotRetrieveElementOverDeletedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model);
      final var elementId = this.idOfElementIn(model, field.get(field.size()-1));
      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.GET, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext, checkpoint);

      }).send(testContext, checkpoint);

    }));

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

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      testRequest(client, HttpMethod.GET, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
        final var retrievedElement = assertThatBodyIs(this.fieldElementClass(), res);
        assertThat(retrievedElement).isNotNull().isEqualTo(element);

      }).send(testContext);

    }));

  }

  /**
   * Should not delete element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteElementOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.DELETE, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

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
  public void shouldNotDeleteElementOverUndefinedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

  }

  /**
   * Should not delete element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteElementOverNullField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(4, vertx, testContext, testContext.succeeding(model -> {

      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    }));

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

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);
      final var checkpoint = testContext.checkpoint(2);
      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res2 -> {

          assertThat(res2.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res2);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).send(testContext, checkpoint);

      }).send(testContext, checkpoint);

    }));

  }

  /**
   * Should not update element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

      testRequest(client, HttpMethod.PUT, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    }));

  }

  /**
   * Should not update element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverUndefinedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not update element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverNullField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not update element if it is deleted.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateElementOverDeletedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size()-1);
      final var elementId = this.idOfElementIn(model, element);
      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }).send(testContext, checkpoint);

    }));

  }

  /**
   * Should not update with a bad JSON element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithBadJsonElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    }));

  }

  /**
   * Should not update with an invalid element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithInvalidElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);

    }));

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

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(11, vertx, testContext, testContext.succeeding(element -> {
        final var modelId = this.idOfModel(model);
        final var field = this.fieldOf(model);
        final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          @SuppressWarnings("unchecked")
          final E updated = (E) assertThatBodyIs(element.getClass(), res);
          this.assertEqualsAdded(element, updated);

          testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model)).expect(resRetrieve -> {

            assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T updatedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
            final var updatedField = this.fieldOf(updatedModel);
            assertThat(updatedField).isNotEmpty().contains(updated);

          }).sendJson(element.toJsonObject(), testContext, checkpoint);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }));

    }));
  }

  /**
   * Should not merge element over if the model is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverUndefinedModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

      testRequest(client, HttpMethod.PATCH, this.modelPath() + this.undefinedModelIdPath() + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(element.toJsonObject(), testContext);

    }));

  }

  /**
   * Should not merge element over if the element is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverUndefinedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not merge element over if the field is {@code null}.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverNullField(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithNullField(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(4, vertx, testContext, testContext.succeeding(element -> {

        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOfModel(model) + this.fieldPath() + this.undefinedElementIdPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext);

      }));

    }));

  }

  /**
   * Should not merge element if it is deleted.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeElementOverDeletedElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var checkpoint = testContext.checkpoint(2);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size()-1);
      final var elementId = this.idOfElementIn(model, element);
      testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(resDelete -> {

        assertThat(resDelete.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }).send(testContext, checkpoint);

    }));

  }

  /**
   * Should not merge with a bad JSON element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithBadJsonElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createBadJsonObjectModelFieldElement(), testContext);

    }));

  }

  /**
   * Should not merge with an invalid element.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithInvalidElement(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      final var modelId = this.idOfModel(model);
      final var field = this.fieldOf(model);
      final var element = field.get(field.size() - 1);
      final var elementId = this.idOfElementIn(model, element);

      testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(this.createInvalidModelFieldElement().toJsonObject(), testContext);

    }));

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

    this.storeValidExampleModelWithFieldElements(4, vertx, testContext, testContext.succeeding(model -> {

      this.createValidModelFieldElementExample(11, vertx, testContext, testContext.succeeding(element -> {
        final var modelId = this.idOfModel(model);
        final var field = this.fieldOf(model);
        final var elementId = this.idOfElementIn(model, field.get(field.size() - 1));
        final var checkpoint = testContext.checkpoint(2);
        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + modelId + this.fieldPath() + "/" + elementId).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          @SuppressWarnings("unchecked")
          final E merged = (E) assertThatBodyIs(element.getClass(), res);
          this.assertEqualsAdded(element, merged);

          testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOfModel(model)).expect(resRetrieve -> {

            assertThat(resRetrieve.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T mergedModel = (T) assertThatBodyIs(model.getClass(), resRetrieve);
            final var mergedField = this.fieldOf(mergedModel);
            assertThat(mergedField).isNotEmpty().contains(merged);

          }).sendJson(element.toJsonObject(), testContext, checkpoint);

        }).sendJson(element.toJsonObject(), testContext, checkpoint);

      }));

    }));
  }
}
