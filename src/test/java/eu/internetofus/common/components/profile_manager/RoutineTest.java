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

import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.StoreServices;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link Routine}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class RoutineTest extends ModelTestCase<Routine> {

  /**
   * Register the necessary services before to test.
   *
   * @param vertx event bus to register the necessary services.
   */
  @BeforeEach
  public void registerServices(final Vertx vertx) {

    WeNetProfileManagerServiceOnMemory.register(vertx);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Routine createModelExample(final int index) {

    final Routine model = new Routine();
    model.user_id = "user_id_" + index;
    model.weekday = "weekday_" + index;
    model.label_distribution = new JsonObject();
    model.label_distribution.put("additional_" + index, new JsonArray().add(new ScoredLabelTest().createModelExample(index).toJsonObject()));
    model.confidence = 1.0 / Math.max(1.0, index + 1);
    return model;
  }

  /**
   * Check that the {@link #createModelExample(int)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldExampleNotBeValid(final Vertx vertx, final VertxTestContext testContext) {

    final Routine model = this.createModelExample(1);
    assertIsNotValid(model, "user_id", vertx, testContext);
  }

  /**
   * Create a valid model example.
   *
   * @param index       of the example to create.
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   * @param handler     to inform about the created routine.
   */
  public void createModelExample(final int index, final Vertx vertx, final VertxTestContext testContext, final Handler<AsyncResult<Routine>> handler) {

    StoreServices.storeProfileExample(index, vertx, testContext, testContext.succeeding(profile -> {

      final Routine model = this.createModelExample(index);
      model.user_id = profile.id;
      handler.handle(Future.succeededFuture(model));
    }));

  }

  /**
   * Check that the {@link #createModelExample(int, Vertx, VertxTestContext, Handler)} is valid.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Routine#validate(String, io.vertx.core.Vertx)
   */
  @Test
  public void shouldComplexExampleBeValid(final Vertx vertx, final VertxTestContext testContext) {

    this.createModelExample(1, vertx, testContext, testContext.succeeding(routine -> {

      assertIsValid(routine, vertx, testContext);
    }));

  }

}
