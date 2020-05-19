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

package eu.internetofus.common.components.service;

import eu.internetofus.common.vertx.ComponentClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * The implementation of the {@link WeNetServiceApiService}.
 *
 * @see WeNetServiceApiService
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetServiceApiServiceImpl extends ComponentClient implements WeNetServiceApiService {

	/**
	 * Create a new service to interact with the WeNet interaction protocol engine.
	 *
	 * @param client to interact with the other modules.
	 * @param conf   configuration.
	 */
	public WeNetServiceApiServiceImpl(WebClient client, JsonObject conf) {

		super(client, conf.getString("service", "https://wenet.u-hopper.com/service"));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonApp(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		this.getJsonObject(retrieveHandler, "/app", id);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void retrieveJsonArrayAppUserIds(String id, Handler<AsyncResult<JsonArray>> retrieveHandler) {

		this.getJsonArray(retrieveHandler, "/app", id, "/users");

	}

}
