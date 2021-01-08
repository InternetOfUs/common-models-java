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
package eu.internetofus.wenet_dummy.api.dummies;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.vertx.ServiceResponseHandlers;
import eu.internetofus.wenet_dummy.persistence.DummiesRepository;
import eu.internetofus.wenet_dummy.service.Dummy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import javax.ws.rs.core.Response.Status;

/**
 * The implementation of the {@link Dummies}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummiesResources implements Dummies {

  /**
   * The event bus that is using.
   */
  protected Vertx vertx;

  /**
   * Create a new instance to provide the services of the {@link Dummies}.
   *
   * @param vertx where resource is defined.
   */
  public DummiesResources(final Vertx vertx) {

    this.vertx = vertx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void postDummy(JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {

    var model = Model.fromJsonObject(body, Dummy.class);
    if (model == null) {

      ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST, "bad_model",
          "The content body does not contains a valid Dummy model.");

    } else {

      DummiesRepository.createProxy(this.vertx).storeDummy(model)
          .onSuccess(stored -> ServiceResponseHandlers.responseWith(resultHandler, Status.CREATED, stored))
          .onFailure(error -> ServiceResponseHandlers.responseWithErrorMessage(resultHandler, Status.BAD_REQUEST,
              "duplicated_id", "The identifier of the new dummy is already defined"));
    }

  }

}
