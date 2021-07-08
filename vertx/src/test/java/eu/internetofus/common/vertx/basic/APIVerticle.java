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

import eu.internetofus.common.vertx.AbstractAPIVerticle;
import eu.internetofus.common.vertx.AbstractServicesVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The verticle to provide the web services.
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
    conf.put(BasicComponentClient.BASIC_CONF_KEY, "http://" + host + ":" + port);
    final var client = AbstractServicesVerticle.createWebClientSession(this.getVertx(), this.config());
    BasicComponent.register(this.vertx, client, conf);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void mountServiceInterfaces(final RouterBuilder routerFactory) {

    routerFactory.mountServiceInterface(Users.class, Users.ADDRESS);
    new ServiceBinder(this.vertx).setAddress(Users.ADDRESS).register(Users.class, new UsersResource(this.vertx));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getOpenAPIResourcePath() {

    return "wenet-basic-openapi.yaml";
  }

}
