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

import eu.internetofus.common.components.Mergeable;
import eu.internetofus.common.components.Merges;
import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.Validable;
import eu.internetofus.common.components.ValidationErrorException;
import eu.internetofus.common.components.Validations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A relationship with another user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "A social relationship with another WeNet user.")
public class SocialNetworkRelationship extends Model implements Validable, Mergeable<SocialNetworkRelationship> {

  /**
   * The identifier of the WeNet user the relationship is related to.
   */
  @Schema(description = "The identifier of the WeNet user the relationship is related to", example = "4c51ee0b-b7ec-4577-9b21-ae6832656e33")
  public String userId;

  /**
   * The relationship type.
   */
  @Schema(description = "The relationship type", example = "friend")
  public SocialNetworkRelationshipType type;

  /**
   * The weight of the relation.
   */
  @Schema(description = "A number from 0 to 1 that indicates the strength of the relation. 0 indicates a deleted/non-exisiting relation.", example = "0.2")
  public Double weight;

  /**
   * Create a new empty relationship.
   */
  public SocialNetworkRelationship() {

  }

  /**
   * Create a new relationship.
   *
   * @param type   of relationship.
   * @param userId identifier of the WeNet user the relationship is related to.
   */
  public SocialNetworkRelationship(final SocialNetworkRelationshipType type, final String userId) {

    this.type = type;
    this.userId = userId;
    this.weight = 1.0d;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<Void> validate(final String codePrefix, final Vertx vertx) {

    final Promise<Void> promise = Promise.promise();
    Future<Void> future = promise.future();
    try {

      if (this.type == null) {

        promise.fail(new ValidationErrorException(codePrefix + ".type", "It is not allowed a social relationship without a type'."));

      } else {

        this.userId = Validations.validateNullableStringField(codePrefix, "userId", 255, this.userId);
        if (this.userId == null) {

          promise.fail(new ValidationErrorException(codePrefix + ".userId", "It is not allowed a social relationship without an user identifier'."));

        } else {

          this.weight = Validations.validateDoubleOnRange(codePrefix, "weight", this.weight, true, 0d, 1d);
          future = future.compose(mapper -> {

            final Promise<Void> searchPromise = Promise.promise();
            WeNetProfileManager.createProxy(vertx).retrieveProfile(this.userId, search -> {

              if (search.result() != null) {

                searchPromise.complete();

              } else {

                searchPromise.fail(new ValidationErrorException(codePrefix + ".userId", "Does not exist any user identifier by '" + this.userId + "'."));
              }

            });
            return searchPromise.future();
          });
          promise.complete();
        }
      }
    } catch (final ValidationErrorException validationError) {

      promise.fail(validationError);
    }

    return future;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<SocialNetworkRelationship> merge(final SocialNetworkRelationship source, final String codePrefix, final Vertx vertx) {

    final Promise<SocialNetworkRelationship> promise = Promise.promise();
    Future<SocialNetworkRelationship> future = promise.future();
    if (source != null) {

      final SocialNetworkRelationship merged = new SocialNetworkRelationship();
      merged.userId = source.userId;
      if (merged.userId == null) {

        merged.userId = this.userId;
      }

      merged.type = source.type;
      if (merged.type == null) {

        merged.type = this.type;
      }

      merged.weight = source.weight;
      if (merged.weight == null) {

        merged.weight = this.weight;
      }

      promise.complete(merged);

      // validate the merged value and set the id
      future = future.compose(Merges.validateMerged(codePrefix, vertx));

    } else {

      promise.complete(this);

    }
    return future;

  }
}
