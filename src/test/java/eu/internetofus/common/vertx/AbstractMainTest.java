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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tinylog.Level;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link AbstractMain}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class AbstractMainTest {

  /**
   * The Main class to test.
   */
  protected DummyMain main;

  /**
   * Mock the {@link AbstractMain} to use on the tests.
   *
   * @param verticle to use.
   */
  @BeforeEach
  public void createMockMain(@Mock final Verticle verticle) {

    this.main = new DummyMain(verticle);
  }

  /**
   * Verify the options are localized.
   *
   * @param lang language to load the options.
   */
  @ParameterizedTest(name = "Should create options for locale {0}")
  @ValueSource(strings = { "en", "es", "ca" })
  public void shouldCreateOptionForLocale(final String lang) {

    final var locale = Locale.getDefault();
    try {

      final var newLocale = new Locale(lang);
      Locale.setDefault(newLocale);
      final var options = this.main.createOptions();
      assertThat(options.hasOption(AbstractMain.HELP_OPTION)).isTrue();
      assertThat(options.hasOption(AbstractMain.VERSION_OPTION)).isTrue();
      assertThat(options.hasOption(AbstractMain.CONF_DIR_OPTION)).isTrue();
      assertThat(options.hasOption(AbstractMain.PROPERTY_OPTION)).isTrue();

    } finally {

      Locale.setDefault(locale);
    }
  }

  /**
   * Verify show version.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system err stream.
   */
  @ExtendWith(SystemErrGuard.class)
  @Test
  public void shouldShowVersion(final VertxTestContext testContext, final Capturable stream) {

    stream.capture();
    this.main.startWith("-" + AbstractMain.VERSION_OPTION).onComplete(testContext.succeeding(context -> testContext.verify(() -> {

      final var data = stream.getCapturedData();
      assertThat(data).contains(Level.INFO.name());
      testContext.completeNow();

    })));

  }

  /**
   * Verify show help message.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system output stream.
   */
  @ExtendWith(SystemOutGuard.class)
  @Test
  public void shouldShowHelpMessage(final VertxTestContext testContext, final Capturable stream) {

    stream.capture();
    this.main.startWith("-" + AbstractMain.HELP_OPTION).onComplete(testContext.succeeding(context -> testContext.verify(() -> {

      final var data = stream.getCapturedData();
      assertThat(data).contains("-" + AbstractMain.HELP_OPTION, "-" + AbstractMain.VERSION_OPTION, "-" + AbstractMain.CONF_DIR_OPTION, "-" + AbstractMain.PROPERTY_OPTION);
      testContext.completeNow();

    })));

  }

  /**
   * Verify undefined argument provokes an error.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system err stream.
   */
  @Test
  @ExtendWith(SystemErrGuard.class)
  public void shouldCaptureUndefinedArgument(final VertxTestContext testContext, final Capturable stream) {

    stream.capture();
    this.main.startWith("-undefined").onComplete(testContext.failing(error -> testContext.verify(() -> {

      final var data = stream.getCapturedData();
      assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());
      testContext.completeNow();

    })));

  }

  /**
   * Verify error happens when the property parameter is wrong.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system err stream.
   */
  @Test
  @ExtendWith(SystemErrGuard.class)
  public void shouldCaptureBadPropertyArgument(final VertxTestContext testContext, final Capturable stream) {

    stream.capture();
    this.main.startWith("-" + AbstractMain.PROPERTY_OPTION, "propertyName").onComplete(testContext.failing(error -> testContext.verify(() -> {

      final var data = stream.getCapturedData();
      assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());
      testContext.completeNow();

    })));

  }

  /**
   * Verify error happens when the configuration directory parameter is wrong.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system err stream.
   */
  @Test
  @ExtendWith(SystemErrGuard.class)
  public void shouldCaptureBadConfDirArgument(final VertxTestContext testContext, final Capturable stream) {

    stream.capture();
    this.main.startWith("-" + AbstractMain.CONF_DIR_OPTION).onComplete(testContext.failing(error -> testContext.verify(() -> {

      final var data = stream.getCapturedData();
      assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());
      testContext.completeNow();

    })));

  }

  /**
   * Called when has to fail to start the WeNet module with the specified arguments.
   *
   * @param testContext test context over the event bus.
   * @param args        to start the weNet module.
   */
  protected void assertNotStartWith(final VertxTestContext testContext, final String... args) {

    this.main.startWith(args).onComplete(testContext.failing(error -> testContext.verify(() -> {

      assertThat(error).isNotNull();
      testContext.completeNow();

    })));

  }

  /**
   * Verify can not start server because exist bad configuration files.
   *
   * @param testContext test context over the event bus.
   * @param stream      captured system err stream.
   * @param tmpDir      temporal directory.
   *
   *
   * @throws Throwable if can not create temporal files.
   */
  @Test
  @ExtendWith(SystemErrGuard.class)
  public void shouldNotStartServerBecauseConfigurationFilesAreWrong(final VertxTestContext testContext, final Capturable stream, @TempDir final File tmpDir) throws Throwable {

    final var confDir = new File(tmpDir, "etc");
    confDir.mkdirs();
    new File(confDir, "Z").mkdirs();
    final var unreadable = new File(confDir, "x.json");
    unreadable.createNewFile();
    unreadable.setReadable(false);
    Files.writeString(new File(confDir, "bad_yaml.yml").toPath(), "{\"api\":{\"port\":0}}");
    Files.writeString(new File(confDir, "bad_json.json").toPath(), "port:0");

    this.assertNotStartWith(testContext, "-" + AbstractMain.CONF_DIR_OPTION, confDir.getAbsolutePath());

  }

  /**
   * Check configuration load from properties.
   *
   * @param testContext test context over the event bus.
   *
   * @throws Throwable if can not create the temporal files.
   */
  @Test
  public void shouldLoadConfigurationProperties(final VertxTestContext testContext) throws Throwable {

    testContext.assertComplete(this.main.startWith("-" + AbstractMain.PROPERTY_OPTION + "api.host=\"HOST\"", "-" + AbstractMain.PROPERTY_OPTION + "api.port=80", "-" + AbstractMain.PROPERTY_OPTION, "persistence.db_name=profile-manager",
        "-" + AbstractMain.PROPERTY_OPTION, "persistence.username=db-user-name", "-" + AbstractMain.PROPERTY_OPTION + " persistence.host=phost", "-" + AbstractMain.PROPERTY_OPTION + "persistence.port=27", "-" + AbstractMain.PROPERTY_OPTION,
        "persistence.db_name=DB_NAME", "-" + AbstractMain.PROPERTY_OPTION + "persistence.username=USER_NAME", "-" + AbstractMain.PROPERTY_OPTION + " persistence.password=PASSWORD", "-" + AbstractMain.PROPERTY_OPTION,
        "webClient.keepAlive=false", "-" + AbstractMain.PROPERTY_OPTION, "webClient.pipelining=true", "-" + AbstractMain.VERSION_OPTION)).onComplete(handler -> {

          final var retriever = ConfigRetriever.create(Vertx.vertx(), this.main.retrieveOptions);
          retriever.getConfig(testContext.succeeding(conf -> testContext.verify(() -> {

            assertThat(conf.getJsonObject("api")).isNotNull();
            assertThat(conf.getJsonObject("api").getString("host")).isEqualTo("HOST");
            assertThat(conf.getJsonObject("api").getInteger("port")).isEqualTo(80);
            assertThat(conf.getJsonObject("persistence")).isNotNull();
            assertThat(conf.getJsonObject("persistence").getString("host")).isEqualTo("phost");
            assertThat(conf.getJsonObject("persistence").getInteger("port")).isEqualTo(27);
            assertThat(conf.getJsonObject("persistence").getString("db_name")).isEqualTo("DB_NAME");
            assertThat(conf.getJsonObject("persistence").getString("username")).isEqualTo("USER_NAME");
            assertThat(conf.getJsonObject("persistence").getString("password")).isEqualTo("PASSWORD");
            assertThat(conf.getJsonObject("webClient")).isNotNull();
            assertThat(conf.getJsonObject("webClient").getBoolean("keepAlive", true)).isFalse();
            assertThat(conf.getJsonObject("webClient").getBoolean("pipelining", false)).isTrue();
            assertThat(conf.getJsonObject("webClient").getBoolean("pipelining", false)).isTrue();

            testContext.completeNow();
          })));

        });

  }

  /**
   * Check configuration load configuration files.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   * @throws Throwable if can not create the temporal files.
   */
  @Test
  public void shouldLoadConfigurationFromFiles(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    final var etc = new File(tmpDir, "etc");
    etc.mkdirs();
    final var api = new JsonObject().put("host", "HOST").put("port", 80);
    final var apiFile = new File(etc, "api.json");
    apiFile.createNewFile();
    Files.writeString(apiFile.toPath(), new JsonObject().put("api", api).encodePrettily());
    final var persistence = new StringBuilder();
    persistence.append("persistence:\n");
    persistence.append("  host: phost\n");
    persistence.append("  port: 27\n");
    persistence.append("  db_name: \"DB_NAME\"\n");
    persistence.append("  username: USER_NAME\n");
    persistence.append("  password: \"PASSWORD\"\n");
    final var persistenceFile = new File(etc, "persistence.yml");
    persistenceFile.createNewFile();
    Files.writeString(persistenceFile.toPath(), persistence.toString());

    testContext.assertComplete(this.main.startWith("-" + AbstractMain.CONF_DIR_OPTION, etc.getAbsolutePath(), "-" + AbstractMain.VERSION_OPTION)).onComplete(handler -> {

      final var retriever = ConfigRetriever.create(Vertx.vertx(), this.main.retrieveOptions);
      retriever.getConfig(testContext.succeeding(conf -> testContext.verify(() -> {

        assertThat(conf.getJsonObject("api")).isNotNull();
        assertThat(conf.getJsonObject("api").getString("host")).isEqualTo("HOST");
        assertThat(conf.getJsonObject("api").getInteger("port")).isEqualTo(80);
        assertThat(conf.getJsonObject("persistence")).isNotNull();
        assertThat(conf.getJsonObject("persistence").getString("host")).isEqualTo("phost");
        assertThat(conf.getJsonObject("persistence").getInteger("port")).isEqualTo(27);
        assertThat(conf.getJsonObject("persistence").getString("db_name")).isEqualTo("DB_NAME");
        assertThat(conf.getJsonObject("persistence").getString("username")).isEqualTo("USER_NAME");
        assertThat(conf.getJsonObject("persistence").getString("password")).isEqualTo("PASSWORD");

        testContext.completeNow();
      })));

    });

  }

  /**
   * Check configuration properties are preferred to the defined on the configuration files.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   * @throws Throwable if can not create the temporal files.
   */
  @Test
  public void shouldConfigureAndUsePropertiesBeforeFiles(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    final var etc = new File(tmpDir, "etc");
    etc.mkdirs();
    final var api = new JsonObject().put("host", "HOST").put("port", 80);
    final var apiFile = new File(etc, "api.json");
    apiFile.createNewFile();
    Files.writeString(apiFile.toPath(), new JsonObject().put("api", api).encodePrettily());
    final var persistence = new StringBuilder();
    persistence.append("persistence:\n");
    persistence.append("  host: phost\n");
    persistence.append("  port: 27\n");
    persistence.append("  db_name: \"DB_NAME\"\n");
    persistence.append("  username: USER_NAME\n");
    persistence.append("  password: \"PASSWORD\"\n");
    final var persistenceFile = new File(etc, "persistence.yml");
    persistenceFile.createNewFile();
    Files.writeString(persistenceFile.toPath(), persistence.toString());

    testContext.assertComplete(this.main.startWith("-" + AbstractMain.CONF_DIR_OPTION + etc.getAbsolutePath(), "-" + AbstractMain.PROPERTY_OPTION + "api.port=8081",
        "-" + AbstractMain.PROPERTY_OPTION + " persistence.db_name=\"database name\"", "-" + AbstractMain.PROPERTY_OPTION, "persistence.password=PASSW0RD", "-" + AbstractMain.VERSION_OPTION)).onComplete(handler -> {

          final var retriever = ConfigRetriever.create(Vertx.vertx(), this.main.retrieveOptions);
          retriever.getConfig(testContext.succeeding(conf -> testContext.verify(() -> {

            assertThat(conf.getJsonObject("api")).isNotNull();
            assertThat(conf.getJsonObject("api").getString("host")).isEqualTo("HOST");
            assertThat(conf.getJsonObject("api").getInteger("port")).isEqualTo(8081);
            assertThat(conf.getJsonObject("persistence")).isNotNull();
            assertThat(conf.getJsonObject("persistence").getString("host")).isEqualTo("phost");
            assertThat(conf.getJsonObject("persistence").getInteger("port")).isEqualTo(27);
            assertThat(conf.getJsonObject("persistence").getString("db_name")).isEqualTo("database name");
            assertThat(conf.getJsonObject("persistence").getString("username")).isEqualTo("USER_NAME");
            assertThat(conf.getJsonObject("persistence").getString("password")).isEqualTo("PASSW0RD");

            testContext.completeNow();
          })));

        });

  }

  /**
   * Should store effective configuration.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   * @throws Throwable if can not create the temporal files.
   */
  @Test
  public void shouldStoreEffectiveConfiguration(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    final var effectiveConf = new File(tmpDir, "conf.json");
    this.main.startWith("-" + AbstractMain.PROPERTY_OPTION + AbstractMain.EFFECTIVE_CONFIGURATION_PATH + "=\"" + effectiveConf.getAbsolutePath() + "\"",
        "-" + AbstractMain.PROPERTY_OPTION + AbstractMain.STORE_EFFECTIVE_CONFIGURATION + "=true", "-" + AbstractMain.PROPERTY_OPTION + "key1=123", "-" + AbstractMain.PROPERTY_OPTION + "key2=\"Two\"",
        "-" + AbstractMain.PROPERTY_OPTION + " persistence.db_name=\"database name\"", "-" + AbstractMain.PROPERTY_OPTION + " api.port=8765", "-" + AbstractMain.PROPERTY_OPTION + "key3=false")
    .onComplete(testContext.succeeding(handler -> testContext.verify(() -> {

      JsonObject storedConf = null;
      JsonObject defaultConf = null;
      try {

        var value = IOUtils.toString(new FileReader(effectiveConf));
        storedConf = new JsonObject(value);

        final var input = this.getClass().getClassLoader().getResourceAsStream(this.main.getDefaultModuleConfigurationResurcePath());
        value = IOUtils.toString(input, Charset.defaultCharset());
        defaultConf = new JsonObject(value);

      } catch (final Throwable t) {

        testContext.failNow(t);
      }

      assertThat(storedConf).isNotNull().isNotEqualTo(defaultConf);
      defaultConf.put("key1", 123);
      defaultConf.put("key2", "Two");
      defaultConf.put("key3", false);
      defaultConf.put(AbstractMain.EFFECTIVE_CONFIGURATION_PATH, effectiveConf.getAbsolutePath());
      defaultConf.put(AbstractMain.STORE_EFFECTIVE_CONFIGURATION, true);
      defaultConf.getJsonObject("api").put("port", 8765);
      defaultConf.getJsonObject("persistence").put("db_name", "database name");
      assertThat(storedConf).isEqualTo(defaultConf);
      testContext.completeNow();

    })));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Promise<Void>> startPromise = ArgumentCaptor.forClass(Promise.class);
    verify(this.main.verticle, timeout(30000).times(1)).start(startPromise.capture());
    startPromise.getValue().complete();

  }

  /**
   * Should not store effective configuration.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   * @throws Throwable if can not create the temporal files.
   */
  @Test
  public void shouldNotStoreEffectiveConfiguration(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    final var effectiveConf = new File(tmpDir, "conf.json");
    this.main.startWith("-" + AbstractMain.PROPERTY_OPTION + AbstractMain.EFFECTIVE_CONFIGURATION_PATH + "=\"" + effectiveConf.getAbsolutePath() + "\"",
        "-" + AbstractMain.PROPERTY_OPTION + AbstractMain.STORE_EFFECTIVE_CONFIGURATION + "=false", "-" + AbstractMain.PROPERTY_OPTION + "key1=123", "-" + AbstractMain.PROPERTY_OPTION + "key2=\"Two\"",
        "-" + AbstractMain.PROPERTY_OPTION + " persistence.db_name=\"database name\"", "-" + AbstractMain.PROPERTY_OPTION + " api.port=8765", "-" + AbstractMain.PROPERTY_OPTION + "key3=false")
    .onComplete(testContext.succeeding(handler -> testContext.verify(() -> {

      try {

        final var value = IOUtils.toString(new FileReader(effectiveConf));
        new JsonObject(value);
        testContext.failNow(new Throwable("Stored effective configuration"));

      } catch (final Throwable t) {

        testContext.completeNow();
      }

    })));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Promise<Void>> startPromise = ArgumentCaptor.forClass(Promise.class);
    verify(this.main.verticle, timeout(30000).times(1)).start(startPromise.capture());
    startPromise.getValue().complete();

  }

  /**
   * Should not start vertx.
   *
   * @param testContext test context over the event bus.
   * @param tmpDir      temporal directory.
   *
   * @throws Throwable if can not start the vertx.
   */
  @Test
  public void shouldNotStartVertx(final VertxTestContext testContext, @TempDir final File tmpDir) throws Throwable {

    this.main.startWith("-" + AbstractMain.PROPERTY_OPTION + AbstractMain.EFFECTIVE_CONFIGURATION_PATH + "=\"" + tmpDir.getAbsolutePath() + "\"").onComplete(testContext.failing(error -> testContext.completeNow()));

    @SuppressWarnings("unchecked")
    final ArgumentCaptor<Promise<Void>> startPromise = ArgumentCaptor.forClass(Promise.class);
    verify(this.main.verticle, timeout(30000).times(1)).start(startPromise.capture());
    startPromise.getValue().fail("No start vertx");

  }

}
