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
package eu.internetofus.wenet_dummy.api.echo;

import eu.internetofus.common.model.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The common CRUD operations.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Echo.PATH)
@Tag(name = "Echo")
@WebApiServiceGen
public interface Echo {

  /**
   * The path to the service.
   */
  String PATH = "/echo";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_dummy.api.echo";

  /**
   * Post a body.
   *
   * @param body          with the model.
   * @param context       where is the model.
   * @param resultHandler the respond to the post action.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Send a dummy model.", description = "Post a dummy model.")
  @RequestBody(description = "The model to post", required = true, content = @Content(schema = @Schema(type = "object")))
  @ApiResponse(responseCode = "201", description = "If the dummy model is stored")
  @ApiResponse(responseCode = "400", description = "Bad model", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void post(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Patch a body.
   *
   * @param body          with the model.
   * @param context       where is the model.
   * @param resultHandler the respond to the patch action.
   */
  @PATCH
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Send a dummy model.", description = "Patch a dummy model.")
  @RequestBody(description = "The model to patch", required = true, content = @Content(schema = @Schema(type = "object")))
  @ApiResponse(responseCode = "201", description = "If the dummy model is stored")
  @ApiResponse(responseCode = "400", description = "Bad model", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void patch(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Put a body.
   *
   * @param body          with the model.
   * @param context       where is the model.
   * @param resultHandler the respond to the put action.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Send a dummy model.", description = "Put a dummy model.")
  @RequestBody(description = "The model to put", required = true, content = @Content(schema = @Schema(type = "object")))
  @ApiResponse(responseCode = "201", description = "If the dummy model is stored")
  @ApiResponse(responseCode = "400", description = "Bad model", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void put(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Post a body.
   *
   * @param body          with the model.
   * @param context       where is the model.
   * @param resultHandler the respond to the post action.
   */
  @POST
  @Path("/dummy")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Send a dummy model.", description = "Post a dummy model.")
  @RequestBody(description = "The model to post", required = true, content = @Content(schema = @Schema(implementation = DummyModel.class)))
  @ApiResponse(responseCode = "201", description = "If the dummy model is stored")
  @ApiResponse(responseCode = "400", description = "Bad model", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
  void postDummyComplex(@Parameter(hidden = true, required = false) JsonObject body,
      @Parameter(hidden = true, required = false) ServiceRequest context,
      @Parameter(hidden = true, required = false) Handler<AsyncResult<ServiceResponse>> resultHandler);

}
