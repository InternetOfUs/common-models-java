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

import eu.internetofus.common.components.MergeFieldLists;
import eu.internetofus.common.model.Mergeable;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import eu.internetofus.common.model.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import java.util.List;
import java.util.UUID;

/**
 * A social practice of an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A social practice of an user.")
public class SocialPractice extends ReflectionModel
    implements Model, Validable, Mergeable<SocialPractice>, Updateable<SocialPractice> {

  /**
   * The identifier of the social practice.
   */
  @Schema(description = "The identifier of the social practice", example = "f9dofgljdksdf", nullable = true)
  public String id;

  /**
   * The descriptor of the social practice.
   */
  @Schema(description = "The descriptor of the social practice", example = "commuter", nullable = true)
  public String label;

  /**
   * The materials necessaries for the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = Material.class), arraySchema = @Schema(description = "The materials necessaries for the social practice", nullable = true))
  public List<Material> materials;

  /**
   * The competences necessaries for the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = Competence.class), arraySchema = @Schema(description = "The competences necessaries for the social practice", nullable = true))
  public List<Competence> competences;

  /**
   * The norms of the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = ProtocolNorm.class), arraySchema = @Schema(description = "The norms of the social practice", nullable = true))
  public List<ProtocolNorm> norms;

  /**
   * Create an empty practice.
   */
  public SocialPractice() {

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
    future = future
        .compose(empty -> Validations.validateNullableStringField(codePrefix, "label", this.label).map(label -> {
          this.label = label;
          return null;
        }));
    future = future.compose(Validations.validate(this.materials,
        (a, b) -> a.name.equals(b.name) && a.classification.equals(b.classification), codePrefix + ".materials",
        vertx));
    future = future.compose(Validations.validate(this.competences,
        (a, b) -> a.name.equals(b.name) && a.ontology.equals(b.ontology), codePrefix + ".competences", vertx));
    future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
    promise.complete();

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialPractice> merge(final SocialPractice source, final String codePrefix, final Vertx vertx) {

    if (source != null) {

      final var merged = new SocialPractice();

      merged.label = source.label;
      if (merged.label == null) {

        merged.label = this.label;
      }

      final Promise<SocialPractice> promise = Promise.promise();
      promise.complete(merged);
      var future = promise.future();
      future = future.compose(MergeFieldLists.mergeMaterials(this.materials, source.materials,
          codePrefix + ".materials", vertx, (model, mergedMaterials) -> {
            model.materials = mergedMaterials;
          }));
      future = future.compose(MergeFieldLists.mergeCompetences(this.competences, source.competences,
          codePrefix + ".competences", vertx, (model, mergedCompetences) -> {
            model.competences = mergedCompetences;
          }));
      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }

      future = future.compose(Validations.validateChain(codePrefix, vertx));
      // When merged set the fixed field values
      future = future.map(mergedValidatedModel -> {

        mergedValidatedModel.id = this.id;
        return mergedValidatedModel;
      });

      return future;

    } else {

      return Future.succeededFuture(this);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialPractice> update(final SocialPractice source, final String codePrefix, final Vertx vertx) {

    if (source != null) {

      final var updated = new SocialPractice();

      updated.label = source.label;
      updated.materials = source.materials;
      updated.competences = source.competences;
      updated.norms = source.norms;

      var future = updated.validate(codePrefix, vertx).map(empty -> updated);

      // When updated set the fixed field values
      future = future.map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

      return future;

    } else {

      return Future.succeededFuture(this);
    }

  }

}
