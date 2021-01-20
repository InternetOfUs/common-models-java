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
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

/**
 * Generic test over a resource that implements CRUD operation over a
 * {@link Model}.
 *
 * @param <T> type of model to do the CRUD operations.
 * @param <I> type of identifier of the model.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractModelResourcesIT<T extends Model, I> {

  /**
   * Return the path to the services to interact with the model.
   *
   * @return the path to the model.
   */
  protected abstract String modelPath();

  /**
   * Should not create a model if the codification is not a valid JSON.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateBadJsonModel(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, this.modelPath()).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(this.createBadJsonObjectModel(), testContext);

  }

  /**
   * Create a {@link JsonObject} that can not be converted to the {@link Model} to
   * test.
   *
   * @return a {@link JsonObject} that not represents a model.
   */
  protected JsonObject createBadJsonObjectModel() {

    return new JsonObject().put("undefinedField", "value");
  }

  /**
   * Create a model that is not valid.
   *
   * @return a model that is not valid.
   */
  protected abstract T createInvalidModel();

  /**
   * Should not create a model that is invalid.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateInvalidModel(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, this.modelPath()).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).sendJson(this.createInvalidModel().toJsonObject(), testContext);

  }

  /**
   * Create an example model that has the specified index.
   *
   * @param index       to use in the example.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @return the future created model.
   */
  protected abstract Future<T> createValidModelExample(final int index, final Vertx vertx,
      final VertxTestContext testContext);

  /**
   * Should not create a model that is invalid.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldCreateModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testRequest(client, HttpMethod.POST, this.modelPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
        @SuppressWarnings("unchecked")
        final T target = (T) assertThatBodyIs(source.getClass(), res);
        this.assertThatCreatedEquals(source, target);

      }).sendJson(source.toJsonObject(), testContext);

    });

  }

  /**
   * Should not create a model with an identifier that is exist.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotCreateModelWithDefinedId(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.POST, this.modelPath()).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(stored.toJsonObject(), testContext);

      });

    });

  }

  /**
   * Store a model that can be retrieved.
   *
   * @param source      model to store.
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @return the future stored model.
   */
  protected abstract Future<T> storeModel(T source, Vertx vertx, VertxTestContext testContext);

  /**
   * Verify created model is equals to the created.
   *
   * @param source the model that is try to create.
   * @param target the created model.
   */
  protected abstract void assertThatCreatedEquals(T source, T target);

  /**
   * Should not retrieve a model with an undefined identifier.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotRetrieveFromUndefinedIdentifier(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, this.modelPath() + this.undefinedModelIdPath()).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isNotEmpty();
      assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

    }).send(testContext);

  }

  /**
   * Return the path to an undefined identifier of the model.
   *
   * @return the path to a model with a bad identifier.
   */
  protected String undefinedModelIdPath() {

    return "/undefined-model-identifier";
  }

  /**
   * Should not retrieve a model with an undefined identifier.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldRetrieveModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.GET, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
          @SuppressWarnings("unchecked")
          final T target = (T) assertThatBodyIs(source.getClass(), res);
          assertThat(target).isEqualTo(stored);

        }).send(testContext);

      });
    });
  }

  /**
   * Return the identifier of a model.
   *
   * @param model to obtain the identifier.
   *
   * @return the model identifier.
   */
  protected abstract I idOf(T model);

  /**
   * Should not update model that is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {
      testRequest(client, HttpMethod.PUT, this.modelPath() + this.undefinedModelIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(source.toJsonObject(), testContext);

    });
  }

  /**
   * Should not update with a bad Model in JSON format.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateBadJsonModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(this.createBadJsonObjectModel(), testContext);

      });

    });
  }

  /**
   * Should not update with an invalid model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithInvalidModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        final var invalid = this.createInvalidModel();
        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(invalid.toJsonObject(), testContext);

      });

    });
  }

  /**
   * Should not update without changes.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotUpdateWithSameModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(stored.toJsonObject(), testContext);

      });

    });
  }

  /**
   * Should update a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldUpdateModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.createValidModelExample(2, vertx, testContext)).onSuccess(target -> {

        testContext.assertComplete(this.storeModel(target, vertx, testContext)).onSuccess(stored -> {

          testRequest(client, HttpMethod.PUT, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T updated = (T) assertThatBodyIs(source.getClass(), res);
            assertThat(updated).isNotNull().isNotEqualTo(stored);
            this.assertThatCreatedEquals(source, updated);

          }).sendJson(source.toJsonObject(), testContext);

        });

      });

    });
  }

  /**
   * Should not merge model that is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {
      testRequest(client, HttpMethod.PATCH, this.modelPath() + this.undefinedModelIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).sendJson(source.toJsonObject(), testContext);

    });
  }

  /**
   * Should not merge with a bad Model in JSON format.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeBadJsonModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(this.createBadJsonObjectModel(), testContext);

      });

    });
  }

  /**
   * Should not merge with an invalid model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithInvalidModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        final var invalid = this.createInvalidModel();
        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(invalid.toJsonObject(), testContext);

      });

    });
  }

  /**
   * Should not merge without changes.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotMergeWithSameModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
          final var error = assertThatBodyIs(ErrorMessage.class, res);
          assertThat(error.code).isNotEmpty();
          assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

        }).sendJson(stored.toJsonObject(), testContext);

      });

    });
  }

  /**
   * Should merge a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldMergeModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.createValidModelExample(2, vertx, testContext)).onSuccess(target -> {

        testContext.assertComplete(this.storeModel(target, vertx, testContext)).onSuccess(stored -> {

          testRequest(client, HttpMethod.PATCH, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

            assertThat(res.statusCode()).isEqualTo(Status.OK.getStatusCode());
            @SuppressWarnings("unchecked")
            final T merged = (T) assertThatBodyIs(source.getClass(), res);
            assertThat(merged).isNotNull().isNotEqualTo(stored);
            this.assertThatCreatedEquals(source, merged);

          }).sendJson(source.toJsonObject(), testContext);

        });

      });

    });
  }

  /**
   * Should not delete model that is not defined.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldNotDeleteUndefinedModel(final Vertx vertx, final WebClient client,
      final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {
      testRequest(client, HttpMethod.DELETE, this.modelPath() + this.undefinedModelIdPath()).expect(res -> {

        assertThat(res.statusCode()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        final var error = assertThatBodyIs(ErrorMessage.class, res);
        assertThat(error.code).isNotEmpty();
        assertThat(error.message).isNotEmpty().isNotEqualTo(error.code);

      }).send(testContext);

    });
  }

  /**
   * Should delete a model.
   *
   * @param vertx       event bus to use.
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldDeleteModel(final Vertx vertx, final WebClient client, final VertxTestContext testContext) {

    testContext.assertComplete(this.createValidModelExample(1, vertx, testContext)).onSuccess(source -> {

      testContext.assertComplete(this.storeModel(source, vertx, testContext)).onSuccess(stored -> {

        testRequest(client, HttpMethod.DELETE, this.modelPath() + "/" + this.idOf(stored)).expect(res -> {

          assertThat(res.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());

        }).send(testContext);

      });

    });
  }

}
