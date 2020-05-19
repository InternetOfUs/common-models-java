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

package eu.internetofus.common.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

/**
 * Abstract verticle to start the services to interact with the other WeNet
 * modules.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractServicesVerticle extends AbstractVerticle {

	/**
	 * The client to do the HTTP request to other components.
	 */
	protected WebClient client;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		try {

			// configure the web client
			final JsonObject webClientConf = this.config().getJsonObject("webClient", new JsonObject());
			final WebClientOptions options = new WebClientOptions(webClientConf);
			this.client = WebClient.create(this.vertx, options);

			final JsonObject serviceConf = this.config().getJsonObject("wenetComponents", new JsonObject());
			this.registerServices(serviceConf);

			startPromise.complete();

		} catch (final Throwable cause) {

			startPromise.fail(cause);
		}
	}

	/**
	 * Called when has to register the services to the Vertx event bus.
	 *
	 * @param serviceConf configuration of the services.
	 *
	 * @throws Exception If can not register or create teh service to register.
	 */
	protected abstract void registerServices(JsonObject serviceConf) throws Exception;

	/**
	 * Close the web client.
	 *
	 * {@inheritDoc}
	 *
	 * @see #client
	 */
	@Override
	public void stop() {

		if (this.client != null) {

			this.client.close();
			this.client = null;
		}
	}

}
