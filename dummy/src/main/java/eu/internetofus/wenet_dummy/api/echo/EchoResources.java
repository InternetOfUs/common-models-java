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
package eu.internetofus.wenet_dummy.api.echo;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;

/**
 * The implementation of the {@link Echo}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class EchoResources implements Echo {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Echo}.
   *
   * @param vertx where resource is defined.
   */
  public EchoResources(final Vertx vertx) {

    this.vertx = vertx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void post(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, body);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void patch(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    ServiceResponseHandlers.responseOk(resultHandler, body);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void put(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    ServiceResponseHandlers.responseOk(resultHandler, body);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void postDummyComplex(final JsonObject body, final ServiceRequest context,
      final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    final var model = Model.fromJsonObject(body, DummyModel.class);
    ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, model);

  }

}
