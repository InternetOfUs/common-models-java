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

package eu.internetofus.common.vertx;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Integration test case over the classes that extends the {@link AbstractMain}
 *
 * @param <T> type of class to test.
 *
 * @see AbstractMain
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public abstract class AbstractMainITC<T extends AbstractMain> {

  /**
   * Create an instance of the main class to test.
   *
   * @return an instance of the main class to use for testing.
   */
  protected abstract T createMain();

  /**
   * Verify can not start server because the port is already binded.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   *
   * @throws Throwable if can not bind a port.
   */
  @Test
  @ExtendWith(SystemErrGuard.class)
  public void shouldNotStartServerBecausePortIsBidded(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    final var socket = new Socket();
    socket.bind(new InetSocketAddress("localhost", 0));
    final var port = socket.getLocalPort();

    final var confDir = new File(tmpDir, "etc");
    confDir.mkdirs();
    Files.writeString(new File(confDir, "host.json").toPath(), "{\"api\":{\"host\":\"localhost\",\"port\":" + port + "}}");

    final var main = this.createMain();
    main.startWith("-" + AbstractMain.CONF_DIR_OPTION, confDir.getAbsolutePath()).onComplete(testContext.failing(error -> testContext.verify(() -> {

      socket.close();
      assertThat(error).isNotNull();
      testContext.completeNow();

    })));

  }

  /**
   * Verify capture exception when configure the configuration directory.
   *
   * @param arguments   that can not be used to start the WeNet Module.
   * @param testContext test context over the event bus.
   */
  @ParameterizedTest(name = "Should not start with the arguments: {0}")
  @ValueSource(strings = { "-" + AbstractMain.PROPERTY_OPTION + "api.host=\"localhost\",-" + AbstractMain.PROPERTY_OPTION + "api.port=\"80\"", "-" + AbstractMain.CONF_DIR_OPTION + "undefined://bad/path/to/conf/dir",
      "-" + AbstractMain.PROPERTY_OPTION + ",persistence.connection_string=\"undefined connection value\"",
      "-" + AbstractMain.PROPERTY_OPTION + ",webClient.keepAlive=false,-" + AbstractMain.PROPERTY_OPTION + ", webClient.pipelining=true" })
  public void shouldNotStartWithBadBasicArguments(final String arguments, final VertxTestContext testContext) {

    final var args = arguments.split(",");
    final var main = this.createMain();
    main.startWith(args).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isNotNull();
      testContext.completeNow();

    })));

  }

}
