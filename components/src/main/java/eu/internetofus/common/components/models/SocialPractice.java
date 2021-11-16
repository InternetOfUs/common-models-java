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
import eu.internetofus.common.model.Merges;
import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import eu.internetofus.common.model.Updateable;
import eu.internetofus.common.model.Validable;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import java.util.List;
import java.util.UUID;

/**
 * A social practice of an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "A social practice of an user.")
public class SocialPractice extends ReflectionModel implements Model, Validable<WeNetValidateContext>,
    Mergeable<SocialPractice, WeNetValidateContext>, Updateable<SocialPractice, WeNetValidateContext> {

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
  public Future<Void> validate(final WeNetValidateContext context) {

    final Promise<Void> promise = Promise.promise();
    var future = promise.future();

    if (this.id == null) {

      this.id = UUID.randomUUID().toString();

    } else {

      this.id = context.normalizeString(this.id);
    }

    this.label = context.normalizeString(this.label);
    future = future.compose(context.validateListField("materials", this.materials, Material::compareIds));
    future = future.compose(context.validateListField("competences", this.competences, Competence::compareIds));
    future = future.compose(context.validateListField("norms", this.norms, ProtocolNorm::compareIds));
    promise.tryComplete();

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialPractice> merge(final SocialPractice source, final WeNetValidateContext context) {

    if (source != null) {

      final var merged = new SocialPractice();

      merged.label = source.label;
      if (merged.label == null) {

        merged.label = this.label;
      }

      final Promise<SocialPractice> promise = Promise.promise();
      promise.complete(merged);
      var future = promise.future();
      future = future.compose(Merges.mergeListField(context, "materials", this.materials, source.materials,
          Material::compareIds, (model, mergedMaterials) -> {
            model.materials = mergedMaterials;
          }));
      future = future.compose(Merges.mergeListField(context, "competences", this.competences, source.competences,
          Competence::compareIds, (model, mergedCompetences) -> {
            model.competences = mergedCompetences;
          }));
      merged.norms = source.norms;
      if (merged.norms == null) {

        merged.norms = this.norms;
      }

      future = future.compose(context.chain());
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
  public Future<SocialPractice> update(final SocialPractice source, final WeNetValidateContext context) {

    if (source != null) {

      final var updated = new SocialPractice();

      updated.label = source.label;
      updated.materials = source.materials;
      updated.competences = source.competences;
      updated.norms = source.norms;

      return Future.succeededFuture(updated).compose(context.chain()).map(updatedValidatedModel -> {

        updatedValidatedModel.id = this.id;
        return updatedValidatedModel;
      });

    } else {

      return Future.succeededFuture(this);
    }

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
  static boolean compareIds(final SocialPractice a, final SocialPractice b) {

    return a != null && a.id != null && a.id.equals(b.id);

  }

}
