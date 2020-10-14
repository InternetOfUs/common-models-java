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

    }

  }

  /**
   * Return the verticle classes to deploy.
   *
   * @return the classes of the verticles to deploy.
   */
  protected abstract Class<? extends AbstractVerticle>[] getVerticleClassesToDeploy();

}
