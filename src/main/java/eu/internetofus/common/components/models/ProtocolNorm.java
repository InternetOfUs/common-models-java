/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Updateable;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A norm used in a protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "ProtocolNorm", description = "The description of a rule that has to follow by the wenet users.")
public class ProtocolNorm extends ReflectionModel
    implements Model, Validable, Mergeable<ProtocolNorm>, Updateable<ProtocolNorm> {

  /**
   * The conditions that fires the norms.
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
   * {@inheritDoc}
   */
  @Override
  public Future<ProtocolNorm> update(final ProtocolNorm source, final String codePrefix, final Vertx vertx) {

    final Promise<ProtocolNorm> promise = Promise.promise();
    var future = promise.future();
    if (source != null) {

      final var updated = new ProtocolNorm();
      updated.description = source.description;
      updated.whenever = source.whenever;
      updated.thenceforth = source.thenceforth;
      updated.ontology = source.ontology;

      future = future.compose(Validations.validateChain(codePrefix, vertx));
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
  public Future<ProtocolNorm> merge(final ProtocolNorm source, final String codePrefix, final Vertx vertx) {

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

      future = future.compose(Validations.validateChain(codePrefix, vertx));
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
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    try {

      this.description = Validations.validateNullableStringField(codePrefix, "description", this.description);
      this.whenever = Validations.validateStringField(codePrefix, "whenever", this.whenever);
      this.thenceforth = Validations.validateStringField(codePrefix, "thenceforth", this.thenceforth);
      this.ontology = Validations.validateNullableStringField(codePrefix, "ontology", this.ontology);
      if (this.whenever.equals(this.thenceforth)) {

        promise.fail(new ValidationErrorException(codePrefix + ".thenceforth",
            "The 'thenceforth' can not be equals to the  'whenever'."));

      } else {

        promise.complete();
      }

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return promise.future();

  }

}
