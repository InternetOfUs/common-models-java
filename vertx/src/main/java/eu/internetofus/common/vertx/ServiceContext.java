/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import javax.validation.constraints.NotNull;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

/**
 * The context of the HTTP context.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ServiceContext {

  /**
   * The information of the request operation.
   */
  @NotNull
  public ServiceRequest request;

  /**
   * The handler to inform of the response.
   */
  @NotNull
  public Handler<AsyncResult<ServiceResponse>> resultHandler;

  /**
   * Create a new context.
   *
   * @param request       information of the request operation.
   * @param resultHandler handler to inform of the response.
   */
  public ServiceContext(@NotNull final ServiceRequest request, @NotNull final Handler<AsyncResult<ServiceResponse>> resultHandler) {

    this.request = request;
    this.resultHandler = resultHandler;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    return this.request.toJson().encodePrettily();

  }
}
