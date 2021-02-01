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
package eu.internetofus.wenet_dummy.api;

import eu.internetofus.common.vertx.AbstractAPIVerticle;
import eu.internetofus.wenet_dummy.api.dummies.Dummies;
import eu.internetofus.wenet_dummy.api.dummies.DummiesResources;
import eu.internetofus.wenet_dummy.api.echo.Echo;
import eu.internetofus.wenet_dummy.api.echo.EchoResources;
import eu.internetofus.wenet_dummy.service.WeNetDummiesClient;
import eu.internetofus.wenet_dummy.service.WeNetDummy;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The dummy API services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class APIVerticle extends AbstractAPIVerticle {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void startedServerAt(final String host, final int port) {

    final var conf = new JsonObject();
    conf.put(WeNetDummiesClient.DUMMY_CONF_KEY, "http://" + host + ":" + port);
    final var client = WebClient.create(this.vertx);
    WeNetDummy.register(this.vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void mountServiceInterfaces(final RouterBuilder routerFactory) {

    routerFactory.mountServiceInterface(Dummies.class, Dummies.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(Dummies.ADDRESS).register(Dummies.class, new DummiesResources(this.vertx));

    routerFactory.mountServiceInterface(Echo.class, Echo.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(Echo.ADDRESS).register(Echo.class, new EchoResources(this.vertx));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getOpenAPIResourcePath() {

    return "wenet-dummy-openapi.yaml";
  }

}
