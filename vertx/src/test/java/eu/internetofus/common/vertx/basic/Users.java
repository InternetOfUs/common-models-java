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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * The definition of the web services to manage the {@link User}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Path(Users.PATH)
@WebApiServiceGen
public interface Users {

  /**
   * The path to the users resource.
   */
  String PATH = "/users";

  /**
   * The address of this service.
   */
  String ADDRESS = "wenet_basic.api.users";

  /**
   * Called when want to create a user.
   *
   * @param body          the new user to create.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  void createUser(JsonObject body, ServiceRequest request, Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get the information of some users.
   *
   * @param offset        index of the first user to return.
   * @param limit         number maximum of users to return.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  void retrieveUsersPage(@DefaultValue("0") @QueryParam(value = "offset") int offset,
      @DefaultValue("10") @QueryParam(value = "limit") int limit, ServiceRequest request,
      Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to get a user.
   *
   * @param userId        identifier of the user to get.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @GET
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  void retrieveUser(@PathParam("userId") String userId, ServiceRequest request,
      Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to update a user.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new user attributes.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @PUT
  @Path("/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  void updateUser(@PathParam("userId") String userId, JsonObject body, ServiceRequest request,
      Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to modify a user.
   *
   * @param userId        identifier of the user to modify.
   * @param body          the new user attributes.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @PATCH
  @Path("/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  void mergeUser(@PathParam("userId") String userId, JsonObject body, ServiceRequest request,
      Handler<AsyncResult<ServiceResponse>> resultHandler);

  /**
   * Called when want to delete a user.
   *
   * @param userId        identifier of the user to delete.
   * @param request       of the query.
   * @param resultHandler to inform of the response.
   */
  @DELETE
  @Path("/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  void deleteUser(@PathParam("userId") String userId, ServiceRequest request,
      Handler<AsyncResult<ServiceResponse>> resultHandler);

}
