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