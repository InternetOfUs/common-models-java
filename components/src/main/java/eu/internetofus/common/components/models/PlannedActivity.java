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

package eu.internetofus.common.components.models;

import eu.internetofus.common.components.profile_manager.WeNetProfileManager;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * An activity planned by an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "An activity planned by an user.")
public class PlannedActivity extends ReflectionModel
    implements Model, Validable, Mergeable<PlannedActivity>, Updateable<PlannedActivity> {

  /**
   * The identifier of the activity.
   */
  @Schema(description = "The identifier of the activity", example = "hfdsfs888", accessMode = AccessMode.READ_ONLY)
  public String id;

  /**
   * The starting time of the activity.
   */
  @Schema(description = "The starting time of the activity", example = "2017-07-21T17:32:00Z", nullable = true)
  public String startTime;

  /**
   * The ending time of the activity.
   */
  @Schema(description = "The ending time of the activity", example = "2019-07-21T17:32:23Z", nullable = true)
  public String endTime;

  /**
   * The description of the activity.
   */
  @Schema(description = "The description of the activity", example = "A few beers for relaxing", nullable = true)
  public String description;

  /**
   * The identifier of other WeNet user taking part to the activity.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The identifier of other wenet user taking part to the activity", example = "[15d85f1d-b1ce-48de-b221-bec9ae954a88]", nullable = true))
  public List<String> attendees;

  /**
   * The current status of the activity.
   */
  @Schema(description = "The current status of the activity", example = "confirmed", nullable = true)
  public PlannedActivityStatus status;

  /**
   * Create an empty activity.
   */
  public PlannedActivity() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id == null) {

      this.id = UUID.randomUUID().toString();
    }
    future = future.compose(empty -> Validations
        .validateNullableStringDateField(codePrefix, "startTime", DateTimeFormatter.ISO_INSTANT, this.startTime)
        .map(startTime -> {
          this.startTime = startTime;
          return null;
        }));
    future = future.compose(empty -> Validations
        .validateNullableStringDateField(codePrefix, "endTime", DateTimeFormatter.ISO_INSTANT, this.endTime)
        .map(endTime -> {
          this.endTime = endTime;
          return null;
        }));
    future = future.compose(empty -> Validations
        .validateNullableStringField(codePrefix, "description", this.description).map(description -> {
          this.description = description;
          return null;
        }));
    future = Validations.composeValidateIds(future, codePrefix, "attendees", this.attendees, true,
        WeNetProfileManager.createProxy(vertx)::retrieveProfile);
    promise.complete();

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<PlannedActivity> merge(final PlannedActivity source, final String codePrefix, final Vertx vertx) {

    final Promise<PlannedActivity> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      // merge the values
      final var merged = new PlannedActivity();

      merged.startTime = source.startTime;
      if (merged.startTime == null) {

        merged.startTime = this.startTime;
      }
      merged.endTime = source.endTime;
      if (merged.endTime == null) {

        merged.endTime = this.endTime;
      }
      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }
      merged.attendees = source.attendees;
      if (merged.attendees == null) {

        merged.attendees = this.attendees;
      }
      merged.status = source.status;
      if (merged.status == null) {

        merged.status = this.status;
      }
      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx)).map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);

    }
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<PlannedActivity> update(final PlannedActivity source, final String codePrefix, final Vertx vertx) {

    final Promise<PlannedActivity> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      // merge the values
      final var updated = new PlannedActivity();

      updated.startTime = source.startTime;
      updated.endTime = source.endTime;
      updated.description = source.description;
      updated.attendees = source.attendees;
      updated.status = source.status;
      promise.complete(updated);

      // validate the merged value and set the id
      future = future.compose(Validations.validateChain(codePrefix, vertx)).map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

    } else {

      promise.complete(this);

    }
    return future;
  }

}
