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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

package eu.internetofus.wenet_dummy;

import eu.internetofus.common.vertx.AbstractMain;
import io.vertx.core.Verticle;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * The main class to provide the dummy services.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class Main extends AbstractMain {

  /**
   * Start the verticles.
   *
   * @param args arguments to configure the main process.
   *
   * @see MainVerticle
   */
  public static void main(final String... args) {

    final var main = new Main();
    main.startWith(args).onComplete(result -> {

      if (!result.succeeded()) {

        InternalLogger.log(Level.ERROR, result.cause(),
            "Can not start the WeNet dummy!\n Check the Logs to known why.");
      }

    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Verticle createMainVerticle() {

    return new MainVerticle();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getModuleName() {

    return "wenet-dummy";
  }

}
