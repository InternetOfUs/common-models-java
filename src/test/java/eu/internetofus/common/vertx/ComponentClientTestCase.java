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

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;

import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.web.VertxWebClientExtension;

/**
 * Generic test over the classes the {@link ComponentClient}.
 *
 * @see ComponentClient
 *
 * @param <T> type of client to test.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, VertxWebClientExtension.class, MockServerExtension.class })
public abstract class ComponentClientTestCase<T extends ComponentClient> {

  /**
   * The mocked server.
   */
  protected ClientAndServer mockedServer;

  /**
   * Create a new test case.
   *
   * @param client to use.
   */
  public ComponentClientTestCase(final ClientAndServer client) {

    this.mockedServer = client;

  }


  /**
   * Create client to test.
   *
   * @param client web client to use.
   */
  protected abstract T createClient(WebClient client);

}
