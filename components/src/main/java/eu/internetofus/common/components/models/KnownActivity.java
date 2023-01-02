/*
  Copyright 2023 UDT-IA, IIIA-CSIC

  Use of this source code is governed by an MIT-style
  license that can be found in the LICENSE file or at
  https://opensource.org/licenses/MIT.
*/
package eu.internetofus.common.components.models;

import eu.internetofus.common.components.WeNetValidateContext;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * The information of an activity that is done by an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "The information of an activity that is done by an user.")
public class KnownActivity extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<KnownActivity, WeNetValidateContext>, Updateable<KnownActivity, WeNetValidateContext> {

  /**
   * The name of the activity.
   */
  @Schema(description = "The name of the activity.", example = "Play piano", nullable = true)
  public String activity;

  /**
   * The instant when the activity was happened.
   */
  @Schema(description = "The time stamp when the activity was happened.", example = "1563871899", nullable = true)
  public Long timestamp;

  /**
   * The confidence of the activity has been done.
   */
  @Schema(description = "The confidence that the activity has been done (value in between 0 and 1, both included)", example = "1", nullable = true)
  public Double confidence;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<KnownActivity> merge(final KnownActivity source, final WeNetValidateContext context) {

    final Promise<KnownActivity> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new KnownActivity();
      merged.activity = source.activity;
      if (merged.activity == null) {

        merged.activity = this.activity;
      }

      merged.timestamp = source.timestamp;
      if (merged.timestamp == null) {

        merged.timestamp = this.timestamp;
      }

      merged.confidence = source.confidence;
      if (merged.confidence == null) {

        merged.confidence = this.confidence;
      }
      promise.complete(merged);

      // Validate the merged value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);
    }
    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    final var future = promise.future();
    this.activity = context.validateStringField("activity", this.activity, promise);
    context.validateTimeStampField("timestamp", this.timestamp, promise);
    context.validateNumberOnRangeField("confidence", this.confidence, 0.0d, 1.0d, promise);
    promise.tryComplete();
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<KnownActivity> update(final KnownActivity source, final WeNetValidateContext context) {

    final Promise<KnownActivity> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new KnownActivity();
      updated.activity = source.activity;
      updated.timestamp = source.timestamp;
      updated.confidence = source.confidence;
      promise.complete(updated);

      // Validate the updated value
      future = future.compose(context.chain());

    } else {

      promise.complete(this);
    }
    return future;

  }

}
