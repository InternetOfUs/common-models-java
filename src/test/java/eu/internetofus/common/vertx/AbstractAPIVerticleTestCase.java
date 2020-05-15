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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import eu.internetofus.common.vertx.AbstractAPIVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;

/**
 * Generic test over the classes that extends the {@link AbstractAPIVerticle}.
 *
 * @param <T> type of verticle to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractAPIVerticleTestCase<T extends AbstractAPIVerticle> {

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldNotStopIfServerNotStarted() {

		final T api = this.createAPIVerticle();
		assertThatCode(() -> api.stop()).doesNotThrowAnyException();

	}

	/**
	 * Return the {@link AbstractAPIVerticle} to test.
	 *
	 * @return the instance of the API verticle to test.
	 */
	protected abstract T createAPIVerticle();

	/**
	 * Check that not stop the server if it is not started.
	 */
	@Test
	public void shouldStopIfServerStarted() {

		final T api = this.createAPIVerticle();
		final HttpServerOptions options = new HttpServerOptions();
		options.setHost("localhost");
		options.setPort(0);
		api.server = Vertx.vertx().createHttpServer(options);
		assertThatCode(() -> api.stop()).doesNotThrowAnyException();
		assertThat(api.server).isNull();

	}

}
