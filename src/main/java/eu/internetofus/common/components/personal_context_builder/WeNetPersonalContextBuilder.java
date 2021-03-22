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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.WeNetComponent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import javax.validation.constraints.NotNull;

/**
 * The class used to interact with the WeNet personal context builder.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ProxyGen
public interface WeNetPersonalContextBuilder extends WeNetComponent {

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_component.PersonalContextBuilder";

  /**
   * Create a proxy of the {@link WeNetPersonalContextBuilder}.
   *
   * @param vertx where the service has to be used.
   *
   * @return the task.
   */
  static WeNetPersonalContextBuilder createProxy(final Vertx vertx) {

    return new WeNetPersonalContextBuilderVertxEBProxy(vertx, WeNetPersonalContextBuilder.ADDRESS);
  }

  /**
   * Register this service.
   *
   * @param vertx  that contains the event bus to use.
   * @param client to do HTTP requests to other services.
   * @param conf   configuration to use.
   */
  static void register(final Vertx vertx, final WebClient client, final JsonObject conf) {

    new ServiceBinder(vertx).setAddress(WeNetPersonalContextBuilder.ADDRESS).register(WeNetPersonalContextBuilder.class,
        new WeNetPersonalContextBuilderClient(client, conf));

  }

  /**
   * {@inheritDoc}
   *
   * ATTENTION: You must to maintains this method to guarantee that VertX
   * generates the code for this method.
   */
  @Override
  void obtainApiUrl(final Handler<AsyncResult<String>> handler);

  /**
   * Return the locations of a set of users.
   *
   * @param users   to obtain the locations.
   * @param handler for inform about the user locations.
   */
  void obtainUserlocations(@NotNull JsonObject users, @NotNull Handler<AsyncResult<JsonObject>> handler);

  /**
   * Return the locations of a set of users.
   *
   * @param users to obtain the locations.
   *
   * @return the future user locations.
   */
  @GenIgnore
  default Future<UsersLocations> obtainUserlocations(@NotNull final Users users) {

    final Promise<JsonObject> promise = Promise.promise();
    this.obtainUserlocations(users.toJsonObject(), promise);
    return Model.fromFutureJsonObject(promise.future(), UsersLocations.class);

  }

}
