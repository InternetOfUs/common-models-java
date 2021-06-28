/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */
package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.model.Model;
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
