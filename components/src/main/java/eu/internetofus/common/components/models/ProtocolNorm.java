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
 * A norm used in a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "ProtocolNorm", description = "The description of a rule that has to follow by the wenet users.")
public class ProtocolNorm extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<ProtocolNorm, WeNetValidateContext>, Updateable<ProtocolNorm, WeNetValidateContext> {

  /**
   * The text that describes what this norm is about.
   */
  @Schema(description = "A human readable description of the norm.", type = "string", nullable = true, example = "Notify to all the participants that the task is closed.")
  public String description;

  /**
   * The conditions that fires the norms.
   */
  @Schema(description = "The conditions that fires this norm.", type = "string", nullable = true, example = "is_received_do_transaction('close',Reason) and not(is_task_closed()) and get_profile_id(Me) and get_task_requester_id(RequesterId) and =(Me,RequesterId) and get_participants(Participants)")
  public String whenever;

  /**
   * The action to do if the conditions are satisfied.
   */
  @Schema(description = "The conditions that fires this norm.", type = "string", nullable = true, example = "add_message_transaction() and close_task() and send_messages(Participants,'close',Reason)")
  public String thenceforth;

  /**
   * The ontology used on the norms.
   */
  @Schema(description = "The ontology used on the norms.", type = "string", nullable = true, example = "get_participants(P) :- get_task_state_attribute(UserIds,'participants',[]), get_profile_id(Me), wenet_remove(P,Me,UserIds).")
  public String ontology;

  /**
   * The priority that defines in which order the norm has to be evaluated.
   */
  @Schema(description = "The order that the norm has to be evaluated. The high value represents the highest priority.", type = "integer", nullable = true, example = "1001")
  public Integer priority;

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolNorm> update(final ProtocolNorm source, final WeNetValidateContext context) {

    final Promise<ProtocolNorm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new ProtocolNorm();
      updated.description = source.description;
      updated.whenever = source.whenever;
      updated.thenceforth = source.thenceforth;
      updated.ontology = source.ontology;

      future = future.compose(context.chain());
      promise.complete(updated);

    } else {

      promise.complete(this);
    }
    return future;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolNorm> merge(final ProtocolNorm source, final WeNetValidateContext context) {

    final Promise<ProtocolNorm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var merged = new ProtocolNorm();
      merged.description = source.description;
      if (merged.description == null) {

        merged.description = this.description;
      }
      merged.whenever = source.whenever;
      if (merged.whenever == null) {

        merged.whenever = this.whenever;
      }
      merged.thenceforth = source.thenceforth;
      if (merged.thenceforth == null) {

        merged.thenceforth = this.thenceforth;
      }

      merged.ontology = source.ontology;
      if (merged.ontology == null) {

        merged.ontology = this.ontology;
      }

      future = future.compose(context.chain());
      promise.complete(merged);

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

    this.description = context.normalizeString(this.description);
    this.whenever = context.validateStringField("whenever", this.whenever, promise);
    this.thenceforth = context.validateStringField("thenceforth", this.thenceforth, promise);
    this.ontology = context.normalizeString(this.ontology);
    if (this.whenever != null && this.whenever.equals(this.thenceforth)) {

      return context.failField("thenceforth", "The 'thenceforth' can not be equals to the 'whenever'.");

    }
    promise.tryComplete();

    return future;

  }

  /**
   * Check if two social practices are equivalent by its identifier fields.
   *
   * @param a first model to compare.
   * @param b second model to compare.
   *
   * @return {@code true} if the social practice can be considered equals by its
   *         identifier.
   */
  public static boolean compareIds(final ProtocolNorm a, final ProtocolNorm b) {

    return a != null && b != null && a.whenever != null && a.thenceforth != null && a.whenever.equals(b.whenever)
        && a.thenceforth.equals(b.thenceforth);

  }

}
