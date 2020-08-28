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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
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

package eu.internetofus.common.components.profile_manager;

import java.util.List;
import java.util.UUID;

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A social practice of an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A social practice of an user.")
public class SocialPractice extends ReflectionModel implements Model, Validable, Mergeable<SocialPractice> {

  /**
   * The identifier of the social practice.
   */
  @Schema(description = "The identifier of the social practice", example = "f9dofgljdksdf")
  public String id;

  /**
   * The descriptor of the social practice.
   */
  @Schema(description = "The descriptor of the social practice", example = "commuter")
  public String label;

  /**
   * The materials necessaries for the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = Material.class), arraySchema = @Schema(description = "The materials necessaries for the social practice"))
  public List<Material> materials;

  /**
   * The competences necessaries for the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = Competence.class), arraySchema = @Schema(description = "The competences necessaries for the social practice"))
  public List<Competence> competences;

  /**
   * The norms of the social practice.
   */
  @ArraySchema(schema = @Schema(implementation = Norm.class), arraySchema = @Schema(description = "The norms of the social practice"))
  public List<Norm> norms;

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

    try {

      this.id = Validations.validateNullableStringField(codePrefix, "id", 255, this.id);
      if (this.id == null) {

        this.id = UUID.randomUUID().toString();
      }

      this.label = Validations.validateNullableStringField(codePrefix, "label", 255, this.label);

      future = future.compose(Validations.validate(this.materials, (a, b) -> a.name.equals(b.name) && a.classification.equals(b.classification), codePrefix + ".materials", vertx));
      future = future.compose(Validations.validate(this.competences, (a, b) -> a.name.equals(b.name) && a.ontology.equals(b.ontology), codePrefix + ".competences", vertx));
      future = future.compose(Validations.validate(this.norms, (a, b) -> a.id.equals(b.id), codePrefix + ".norms", vertx));
      promise.complete();

    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

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

      var future = merged.validate(codePrefix, vertx).map(empty -> merged);

      future = future.compose(Merges.mergeMaterials(this.materials, source.materials, codePrefix + ".materials", vertx, (model, mergedMaterials) -> {
        model.materials = mergedMaterials;
      }));
      future = future.compose(Merges.mergeCompetences(this.competences, source.competences, codePrefix + ".competences", vertx, (model, mergedCompetences) -> {
        model.competences = mergedCompetences;
      }));
      future = future.compose(Merges.mergeNorms(this.norms, source.norms, codePrefix + ".norms", vertx, (model, mergedNorms) -> {
        model.norms = mergedNorms;
      }));
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

}
