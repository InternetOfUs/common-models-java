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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.components.ErrorMessage;
import eu.internetofus.common.components.Model;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.junit5.VertxTestContext;
import io.vertx.serviceproxy.ServiceException;
import javax.ws.rs.core.Response.Status;

/**
 * Assert to use when check a {@link ComponentClient}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface ComponentClientAsserts {

  /**
   * Assert that the client call fails with an HTTP status error and an
   * {@code ErrorMessage} as content.
   *
   * @param future      that has to fail.
   * @param testContext context of the test.
   * @param status      expected status error.
   *
   * @return the future error message for the HTTP failure.
   */
  static Future<ErrorMessage> assertStatusError(final Future<?> future, final VertxTestContext testContext,
      final Status status) {

    final Promise<ErrorMessage> promise = Promise.promise();
    testContext.assertFailure(future).onFailure(error ->

    testContext.verify(() -> {

      assertThat(error).isExactlyInstanceOf(ServiceException.class);
      final var serviceError = (ServiceException) error;
      assertThat(serviceError.failureCode()).isEqualTo(status.getStatusCode());
      final var errorMsg = Model.fromJsonObject(serviceError.getDebugInfo(), ErrorMessage.class);
      assertThat(errorMsg).isNotNull();
      promise.complete(errorMsg);

    }));
    return promise.future();

  }

}
