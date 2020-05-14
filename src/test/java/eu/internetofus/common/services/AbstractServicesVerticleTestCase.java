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

package eu.internetofus.common.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * General test over the classes that extends the
 * {@link AbstractServicesVerticle}.
 *
 * @param <T> type of services verticle to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractServicesVerticleTestCase<T extends AbstractServicesVerticle> {

	/**
	 * Create the verticle to start the services.
	 *
	 * @return the instance of the services verticle to test.
	 */
	protected abstract T createServicesVerticle();

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldNotStopIfServerNotStarted() {

		final T service = this.createServicesVerticle();
		assertThatCode(() -> service.stop()).doesNotThrowAnyException();

	}

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldStopIfServerStarted() {

		final T service = this.createServicesVerticle();
		service.client = WebClient.create(Vertx.vertx(), new WebClientOptions(new JsonObject()));
		assertThatCode(() -> service.stop()).doesNotThrowAnyException();
		assertThat(service.client).isNull();

	}

	/**
	 * Check that not start verticle that is not deployed.
	 *
	 * @param testContext context to manage the test.
	 *
	 * @throws Exception If it can not start the verticle.
	 */
	@Test
	@ExtendWith(VertxExtension.class)
	public void shouldNotStartUndeployedVerticle(VertxTestContext testContext) throws Exception {

		final T service = this.createServicesVerticle();
		final Promise<Void> startPromise = Promise.promise();
		service.start(startPromise);
		startPromise.future().onComplete(start -> {

			testContext.verify(() -> {
				assertThat(start.failed()).isTrue();
				assertThat(start.cause()).isNotNull();
			});
			testContext.completeNow();
		});

	}

}
