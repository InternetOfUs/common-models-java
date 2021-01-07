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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.vertx.AbstractMain;
import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link Main}
 *
 * @see Main
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MainTest {

  /**
   * Verify show help message from calling main.
   *
   * @param stream captured system output stream.
   */
  @ExtendWith(SystemOutGuard.class)
  @Test
  public void shouldShowHelpMessageFromMain(final Capturable stream) {

    stream.capture();
    Main.main("-" + AbstractMain.HELP_OPTION);
    final var data = stream.getCapturedData();
    assertThat(data).contains("-" + AbstractMain.HELP_OPTION, "-" + AbstractMain.VERSION_OPTION,
        "-" + AbstractMain.CONF_DIR_OPTION, "-" + AbstractMain.PROPERTY_OPTION);

  }

  /**
   * Verify not start form main.
   *
   * @param stream captured system output stream.
   */
  @ExtendWith(SystemErrGuard.class)
  @Test
  public void shouldNotStartFromMainFunction(final Capturable stream) {

    stream.capture();
    Main.main("-undefined");
    final var data = stream.getCapturedData();
    assertThat(data).contains("Can not start the WeNet dummy!");

  }

}