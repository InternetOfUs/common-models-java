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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public void start(Promise<Void> startPromise) throws Exception {

		final List<Class<? extends AbstractVerticle>> verticlesToDeploy = new ArrayList<>(
				Arrays.asList(this.getVerticleClassesToDeploy()));

		this.deployNextVerticle(verticlesToDeploy, startPromise);

	}

	/**
	 * DEploy the next verticle.
	 *
	 * @param verticlesToDeploy the list of verticles to deploy.
	 * @param startPromise      promise to inform when all the verticles has been
	 *                          deployed.
	 */
	private void deployNextVerticle(List<Class<? extends AbstractVerticle>> verticlesToDeploy,
			Promise<Void> startPromise) {

		if (verticlesToDeploy.isEmpty()) {

			startPromise.complete();

		} else {

			final Class<? extends AbstractVerticle> verticle = verticlesToDeploy.remove(0);
			final DeploymentOptions options = new DeploymentOptions(this.config()).setConfig(this.config());
			if (verticle.isAnnotationPresent(Worker.class)) {

				options.setWorker(true);
			}
			this.vertx.deployVerticle(verticle, options, deploy -> {

				if (deploy.failed()) {

					startPromise.fail(deploy.cause());

				} else {

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
