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

package eu.internetofus.common.components.profile_manager;

import eu.internetofus.common.vertx.ComponentClientWithCache;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * The client to interact with the {@link WeNetProfileManager}.
 *
 * @see WeNetProfileManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetProfileManagerClient extends ComponentClientWithCache implements WeNetProfileManager {

  /**
   * The default URL to bind the client.
   */
  public static final String DEFAULT_PROFILE_MANAGER_API_URL = "https://wenet.u-hopper.com/prod/profile_manager";

  /**
   * The name of the configuration property that contains the URL to the profile
   * manager API.
   */
  public static final String PROFILE_MANAGER_CONF_KEY = "profileManager";

  /**
   * Create a new service to interact with the WeNet profile manager.
   *
   * @param client to interact with the other modules.
   * @param conf   configuration.
   */
  public WeNetProfileManagerClient(final WebClient client, final JsonObject conf) {

    super(client, conf, PROFILE_MANAGER_CONF_KEY, DEFAULT_PROFILE_MANAGER_API_URL);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createProfile(final JsonObject profile, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(profile, "/profiles").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveProfile(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/profiles", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteProfile(final String id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/profiles", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createCommunity(final JsonObject community, final Handler<AsyncResult<JsonObject>> handler) {

    this.post(community, "/communities").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunity(final String id, final Handler<AsyncResult<JsonObject>> handler) {

    this.getJsonObject("/communities", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteCommunity(final String id, final Handler<AsyncResult<Void>> handler) {

    this.delete("/communities/", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveCommunityProfilesPage(final String appId, final String name, final String description,
      final String keywords, final String members, final String order, final int offset, final int limit,
      final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (name != null) {

      params.put("name", name);
    }
    if (description != null) {

      params.put("description", description);
    }
    if (keywords != null) {

      params.put("keywords", keywords);
    }
    if (members != null) {

      params.put("members", members);
    }
    if (order != null) {

      params.put("order", order);
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/communities").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCommunity(@NotNull final String id, @NotNull final JsonObject community,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.put(community, "/communities", id).onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateProfile(@NotNull final String id, @NotNull final JsonObject profile,
      final Boolean storeProfileChangesInHistory, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    if (storeProfileChangesInHistory != null) {

      final Map<String, String> queryParams = new HashMap<>();
      queryParams.put("storeProfileChangesInHistory", String.valueOf(storeProfileChangesInHistory));
      this.put(profile, queryParams, "/profiles", id).onComplete(handler);

    } else {

      this.put(profile, "/profiles", id).onComplete(handler);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getUserIdentifiersPage(final int offset, final int limit,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/userIdentifiers").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void operationDiversity(@NotNull final JsonObject data,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(data, "/operations/diversity").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void operationSimilarity(@NotNull final JsonObject data,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(data, "/operations/similarity").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isProfileDefined(final String id, @NotNull final Handler<AsyncResult<Boolean>> handler) {

    this.headWithCache("/profiles", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void isCommunityDefined(final String id, @NotNull final Handler<AsyncResult<Boolean>> handler) {

    this.headWithCache("/communities", id).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrieveSocialNetworkRelationshipsPage(final String appId, final String sourceId, final String targetId,
      final String type, final Double weightFrom, final Double weightTo, final String order, final int offset,
      final int limit, final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (sourceId != null) {

      params.put("sourceId", sourceId);
    }
    if (targetId != null) {

      params.put("targetId", targetId);
    }
    if (type != null) {

      params.put("type", type);
    }
    if (order != null) {

      params.put("order", order);
    }
    if (weightFrom != null) {

      params.put("weightFrom", String.valueOf(weightFrom));
    }
    if (weightTo != null) {

      params.put("weightTo", String.valueOf(weightTo));
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/relationships").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOrUpdateSocialNetworkRelationship(@NotNull final JsonObject relationship,
      @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.put(relationship, "/relationships").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOrUpdateSocialNetworkRelationships(@NotNull final JsonArray relationships,
      @NotNull final Handler<AsyncResult<JsonArray>> handler) {

    this.put(relationships, "/relationships/batch").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteSocialNetworkRelationships(final String appId, final String sourceId, final String targetId,
      final String type, final Double weightFrom, final Double weightTo, final Handler<AsyncResult<Void>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (sourceId != null) {

      params.put("sourceId", sourceId);
    }
    if (targetId != null) {

      params.put("targetId", targetId);
    }
    if (type != null) {

      params.put("type", type);
    }
    if (weightFrom != null) {

      params.put("weightFrom", String.valueOf(weightFrom));
    }
    if (weightTo != null) {

      params.put("weightTo", String.valueOf(weightTo));
    }
    this.delete(params, "/relationships").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void taskDeleted(@NotNull final String taskId, @NotNull final Handler<AsyncResult<Void>> handler) {

    this.delete("/task", taskId).onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getProfileHistoricPage(final String userId, final Long from, final Long to, final String order,
      final int offset, final int limit, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (from != null) {

      params.put("from", String.valueOf(from));
    }
    if (to != null) {

      params.put("to", String.valueOf(to));
    }
    if (order != null) {

      params.put("order", order);
    }
    params.put("offset", String.valueOf(offset));
    params.put("limit", String.valueOf(limit));
    this.getJsonObject(params, "/users", userId, "/historic").onComplete(handler);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTrustEvent(final JsonObject event, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    this.post(event, "/trusts/rating").onComplete(handler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateTrust(final String sourceId, final String targetId, final String appId, final String communityId,
      final String taskTypeId, final String taskId, final String relationship, final Long reportFrom,
      final Long reportTo, final TrustAggregator aggregator, @NotNull final Handler<AsyncResult<JsonObject>> handler) {

    final var params = new LinkedHashMap<String, String>();
    if (appId != null) {

      params.put("appId", appId);
    }
    if (communityId != null) {

      params.put("communityId", communityId);
    }
    if (taskTypeId != null) {

      params.put("taskTypeId", taskTypeId);
    }
    if (taskId != null) {

      params.put("taskId", taskId);
    }
    if (relationship != null) {

      params.put("relationship", relationship);
    }
    if (reportFrom != null) {

      params.put("reportFrom", String.valueOf(reportFrom));
    }
    if (reportTo != null) {

      params.put("reportTo", String.valueOf(reportTo));
    }
    if (aggregator != null) {

      params.put("aggregator", aggregator.name());
    }
    this.getJsonObject(params, "/trusts", sourceId, "/with", targetId).onComplete(handler);

  }

}
