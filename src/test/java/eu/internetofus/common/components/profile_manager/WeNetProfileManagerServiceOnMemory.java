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

package eu.internetofus.common.components.profile_manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.internetofus.common.components.Model;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Implementation of the {@link WeNetProfileManagerService} that can be used for
 * unit testing.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerServiceOnMemory implements WeNetProfileManagerService {

	/**
	 * Register this service.
	 *
	 * @param vertx that contains the event bus to use.
	 */
	public static void register(Vertx vertx) {

		new ServiceBinder(vertx).setAddress(WeNetProfileManagerService.ADDRESS).register(WeNetProfileManagerService.class,
				new WeNetProfileManagerServiceOnMemory());

	}

	/**
	 * The profiles that has been stored on the service.
	 */
	private final Map<String, JsonObject> profiles;

	/**
	 * Create the service.
	 */
	public WeNetProfileManagerServiceOnMemory() {

		this.profiles = new HashMap<>();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void createProfile(JsonObject profile, Handler<AsyncResult<JsonObject>> createHandler) {

		final WeNetUserProfile model = Model.fromJsonObject(profile, WeNetUserProfile.class);
		if (model == null) {
			// bad profile
			createHandler.handle(Future.failedFuture("Bad profile to store"));

		} else {

			String id = profile.getString("id");
			if (id == null) {

				id = UUID.randomUUID().toString();
				profile.put("id", id);
			}

			if (this.profiles.containsKey(id)) {

				createHandler.handle(Future.failedFuture("Profile already registered"));

			} else {

				this.profiles.put(id, profile);
				createHandler.handle(Future.succeededFuture(profile));
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void retrieveJsonProfile(String id, Handler<AsyncResult<JsonObject>> retrieveHandler) {

		final JsonObject profile = this.profiles.get(id);
		if (profile == null) {

			retrieveHandler.handle(Future.failedFuture("No Profile associated to the ID"));

		} else {

			retrieveHandler.handle(Future.succeededFuture(profile));

		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void deleteProfile(String id, Handler<AsyncResult<JsonObject>> deleteHandler) {

		final JsonObject profile = this.profiles.remove(id);
		if (profile == null) {

			deleteHandler.handle(Future.failedFuture("No Profile associated to the ID"));

		} else {

			deleteHandler.handle(Future.succeededFuture());

		}

	}

}
