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
package eu.internetofus.common.vertx.basic;

import static eu.internetofus.common.vertx.HttpResponses.assertThatBodyIs;
import static io.reactiverse.junit5.web.TestRequest.queryParam;
import static io.reactiverse.junit5.web.TestRequest.testRequest;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.ErrorMessage;
import eu.internetofus.common.vertx.BadRequestHandler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the integration of the {@link BadRequestHandler}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(BasicIntegrationExtension.class)
public class BadRequestHandlerIT {

  /**
   * Should not create a user when the body is not valid.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldInformAboutBadBodyFieldWhenCreateUser(final WebClient client, final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Users.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_body_name");
      assertThat(error.message).isEqualTo("input don't match type STRING");

    }).sendJson(new JsonObject().put("name", true), testContext);

  }

  /**
   * Should not create a user when the body is not valid.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldInformAboutBadBodyWithExtraValuesWhenCreateUser(final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.POST, Users.PATH).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_user");
      assertThat(error.message).contains("user", "undefined").doesNotContain("class", User.class.getName());

    }).sendJson(new JsonObject().put("name", "name").put("undefined", "undefined"), testContext);

  }

  /**
   * Should not create a user when the body is not valid.
   *
   * @param client      to connect to the server.
   * @param testContext context to test.
   */
  @Test
  public void shouldInformAboutBadParameterWhenGetUsersPage(final WebClient client,
      final VertxTestContext testContext) {

    testRequest(client, HttpMethod.GET, Users.PATH).with(queryParam("limit", "hundred")).expect(res -> {

      assertThat(res.statusCode()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
      final var error = assertThatBodyIs(ErrorMessage.class, res);
      assertThat(error.code).isEqualTo("bad_parameter_limit");
      assertThat(error.message).contains("NumberFormatException");

    }).send(testContext);

  }

}
