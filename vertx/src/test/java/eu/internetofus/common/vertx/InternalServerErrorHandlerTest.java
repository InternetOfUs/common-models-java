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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Integration test of the {@link InternalServerErrorHandler}.
 *
 * @see InternalServerErrorHandler
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class InternalServerErrorHandlerTest {

  /**
   * Verify that build handler.
   */
  @Test
  public void shouldBuildHandler() {

    final var handler = InternalServerErrorHandler.build();
    assertThat(handler).isNotNull();

  }

  /**
   * Verify that return a bar request error.
   */
  @Test
  public void shouldReturnBadRequestErrorMessage() {

    final var handler = new InternalServerErrorHandler();
    final var event = mock(RoutingContext.class);
    final var response = mock(HttpServerResponse.class);
    doReturn(response).when(event).response();
    handler.handle(event);
    verify(response, times(1)).setStatusCode(500);
    verify(response, times(1)).putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    verify(response, times(1)).end("{\"code\":\"unexpected_error\",\"message\":\"Internal server error\"}");

  }

  /**
   * Verify that return a bar request error.
   */
  @Test
  public void shouldReturnBadRequestErrorMessageWithFailedMessage() {

    final var handler = new InternalServerErrorHandler();
    final var event = mock(RoutingContext.class);
    final var response = mock(HttpServerResponse.class);
    doReturn(response).when(event).response();
    doReturn(new Throwable("Error message")).when(event).failure();
    handler.handle(event);
    verify(response, times(1)).setStatusCode(500);
    verify(response, times(1)).putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    verify(response, times(1)).end("{\"code\":\"unexpected_error\",\"message\":\"Error message\"}");

  }

}
