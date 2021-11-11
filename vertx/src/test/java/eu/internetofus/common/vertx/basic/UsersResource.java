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
package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.model.DummyValidateContext;
import eu.internetofus.common.model.ValidationErrorException;
import eu.internetofus.common.vertx.ModelContext;
import eu.internetofus.common.vertx.ModelResources;
import eu.internetofus.common.vertx.ServiceContext;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;
import org.tinylog.Logger;

/**
 * The implementation of the {@link Users}.
 *
 * @see Users
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UsersResource implements Users {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Users}.
   *
   * @param vertx where resource is defined.
   */
  public UsersResource(final Vertx vertx) {

    this.vertx = vertx;
  }

  /**
   * Create the user context.
   *
   * @return the context of the {@link User}.
   */
  protected ModelContext<User, String, DummyValidateContext> createUserContext() {

    final var context = new ModelContext<User, String, DummyValidateContext>();
    context.name = "user";
    context.type = User.class;
    context.validateContext = new DummyValidateContext("bad_user");
    return context;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveUser(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createUserContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.retrieveModel(model,
        (id, hanlder) -> UsersRepository.createProxy(this.vertx).searchUser(id).onComplete(hanlder), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createUser(final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createUserContext();
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.createModel(body, model,
        (user, handler) -> UsersRepository.createProxy(this.vertx).storeUser(user).onComplete(handler), context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateUser(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createUserContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.updateModel(body, model,
        (id, hanlder) -> UsersRepository.createProxy(this.vertx).searchUser(id).onComplete(hanlder),
        (user, handler) -> UsersRepository.createProxy(this.vertx).updateUser(user).onComplete(handler), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mergeUser(final String userId, final JsonObject body, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createUserContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.mergeModel(body, model,
        (id, hanlder) -> UsersRepository.createProxy(this.vertx).searchUser(id).onComplete(hanlder),
        (user, handler) -> UsersRepository.createProxy(this.vertx).updateUser(user).onComplete(handler), context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUser(final String userId, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = this.createUserContext();
    model.id = userId;
    final var context = new ServiceContext(request, resultHandler);
    ModelResources.deleteModel(model, UsersRepository.createProxy(this.vertx)::deleteUser, context);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveUsersPage(final int offset, final int limit, final ServiceRequest request,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    try {

      UsersRepository.createProxy(this.vertx).retrieveUsersPage(offset, limit).onComplete(retrieve -> {

        if (retrieve.failed()) {

          final var cause = retrieve.cause();
          Logger.debug(cause, "GET /users => Retrieve error");
          ServiceResponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, cause);

        } else {

          final var usersPage = retrieve.result();
          Logger.debug("GET /users => {}.", usersPage);
          ServiceResponseHandlers.responseOk(resultHandler, usersPage);
        }

      });

    } catch (final ValidationErrorException error) {

      Logger.debug(error, "GET /users => Retrieve error");
      ServiceResponseHandlers.responseFailedWith(resultHandler, Status.BAD_REQUEST, error);

    }

  }

}
