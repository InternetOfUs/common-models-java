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

import static eu.internetofus.common.model.ValidableAsserts.assertIsNotValid;
import static eu.internetofus.common.model.ValidableAsserts.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ModelTestCase;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test the {@link IncentiveBadge}.
 *
 * @see IncentiveBadge
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class IncentiveBadgeTest extends ModelTestCase<IncentiveBadge> {

  /**
   * {@inheritDoc}
   */
  @Override
  public IncentiveBadge createModelExample(final int index) {

    final var model = new IncentiveBadge();
    model.BadgeClass = "class " + index;
    model.ImgUrl = "http://3.126.161.118:8000/media/uploads/badges/assertion-OYmfmtDFSIKG-qeZfXz4QQ.png?index=" + index;
    model.Criteria = "criteria " + index;
    model.Message = "message " + index;
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param index       to verify
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @ParameterizedTest(name = "The model example {0} has to be valid")
  @ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
  public void shouldExampleBeValid(final int index, final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(index);
    final var expected = Model.fromJsonObject(model.toJsonObject(), IncentiveBadge.class);
    model.BadgeClass = "   \n  " + model.BadgeClass + "   \n";
    model.Criteria = "   \n  " + model.Criteria + "   \n";
    model.ImgUrl = "   \n  " + model.ImgUrl + "   \n";
    model.Message = "   \n  " + model.Message + "   \n";
    assertIsValid(model, vertx, testContext, () -> {

      assertThat(model).isEqualTo(expected);
    });

  }

  /**
   * Check that not accept a bad image URL.
   *
   * @param vertx       event bus to use.
   * @param testContext context to test.
   *
   * @see WeNetUserProfile#validate(String, Vertx)
   */
  @Test
  public void shouldNotBeValidWithABadImgUrl(final Vertx vertx, final VertxTestContext testContext) {

    final var model = this.createModelExample(1);
    model.ImgUrl = "@·hkjhoj//.lhfohoñ\\ohpiuv";
    assertIsNotValid(model, "ImgUrl", vertx, testContext);
  }

}
