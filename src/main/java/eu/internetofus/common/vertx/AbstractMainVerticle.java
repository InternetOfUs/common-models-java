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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

/**
 * The common verticle to start the component of the WeNet Module.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractMainVerticle extends AbstractVerticle {

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {

    final List<Class<? extends AbstractVerticle>> verticlesToDeploy = new ArrayList<>(Arrays.asList(this.getVerticleClassesToDeploy()));

    this.deployNextVerticle(verticlesToDeploy, startPromise);

  }

  /**
   * Deploy the next verticle.
   *
   * @param verticlesToDeploy the list of verticles to deploy.
   * @param startPromise      promise to inform when all the verticles has been deployed.
   */
  private void deployNextVerticle(final List<Class<? extends AbstractVerticle>> verticlesToDeploy, final Promise<Void> startPromise) {

    if (verticlesToDeploy.isEmpty()) {

      startPromise.complete();

    } else {

      final Class<? extends AbstractVerticle> verticle = verticlesToDeploy.remove(0);
      try {

        final var conf = this.config();
        final var options = new DeploymentOptions(conf).setConfig(conf);
        if (verticle.isAnnotationPresent(Worker.class)) {

          options.setWorker(true);
        }
        Logger.trace("Deploying verticle {}", verticle);
        this.getVertx().deployVerticle(verticle, options, deploy -> {

          if (deploy.failed()) {

            final var cause = deploy.cause();
            Logger.error(cause, "Cannot deploy verticle {}", verticle);
            startPromise.fail(cause);

          } else {

            Logger.trace("Deployed verticle {}", verticle);
            this.deployNextVerticle(verticlesToDeploy, startPromise);
          }
        });

      } catch (final Throwable t) {

        Logger.error(t, "Cannot deploy verticle {}", verticle);
        startPromise.fail(t);

      }
    }

  }

  /**
   * Return the verticle classes to deploy.
   *
   * @return the classes of the verticles to deploy.
   */
  protected abstract Class<? extends AbstractVerticle>[] getVerticleClassesToDeploy();

}
