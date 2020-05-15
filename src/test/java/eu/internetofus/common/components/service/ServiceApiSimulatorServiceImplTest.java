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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.Containers;
import eu.internetofus.common.vertx.WeNetModuleContext;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;

/**
 * Test the {@link ServiceApiSimulatorServiceImpl}
 *
 * @see ServiceApiSimulatorServiceImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class ServiceApiSimulatorServiceImplTest extends ServiceApiSimulatorServiceTestCase {

	/**
	 * Create the context to use.
	 *
	 * @param vertx that contains the event bus to use.
	 */
	@BeforeEach
	public void startServiceApiSimulator(Vertx vertx) {

		final int serviceApiPort = Containers.nextFreePort();
		Containers.createAndStartServiceApiSimulator(serviceApiPort);
		final JsonObject configuration = new JsonObject().put("wenetComponents", new JsonObject().put("service",
				new JsonObject().put("host", "localhost").put("port", serviceApiPort).put("apiPath", "")));
		final WeNetModuleContext context = new WeNetModuleContext(vertx, configuration);
		ServiceApiSimulatorService.register(context);
	}

}
