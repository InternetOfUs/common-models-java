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

package eu.internetofus.common.vertx;

import java.util.ArrayList;
import java.util.List;

import eu.internetofus.common.components.DummyComplexModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Used to manage a set of {@link DummyComplexModel}.
 *
 * @see DummyComplexModel
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class DummyComplexModelRepository {

  /**
   * The stored models.
   */
  protected List<DummyComplexModel> models;

  /**
   * Create a new repository.
   */
  public DummyComplexModelRepository() {

    this.models = new ArrayList<>();
  }

  /**
   * Retrieve the model defined with the specified id.
   *
   * @param id      of the model to obtain.
   * @param handler to inform of the found model.
   */
  public void retrieve(final String id, final Handler<AsyncResult<DummyComplexModel>> handler) {

    for (final var model : this.models) {

      if (model.id.equals(id)) {

        handler.handle(Future.succeededFuture(model));
        return;
      }
    }

    handler.handle(Future.failedFuture("Not found"));

  }

  /**
   * Update the model defined with the specified id.
   *
   * @param source  model to update.
   * @param handler to inform of the update process.
   */
  public void update(final DummyComplexModel source, final Handler<AsyncResult<Void>> handler) {

    for (var i = 0; i < this.models.size(); i++) {

      if (source.id.equals(this.models.get(i).id)) {

        this.models.remove(i);
        this.models.add(i, source);
        handler.handle(Future.succeededFuture());
        return;
      }
    }

    handler.handle(Future.failedFuture("Not found"));

  }

}
