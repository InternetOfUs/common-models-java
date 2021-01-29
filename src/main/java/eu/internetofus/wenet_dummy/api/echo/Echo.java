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
package eu.internetofus.wenet_dummy.api.echo;

import eu.internetofus.common.components.ErrorMessage;
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
