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

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Locale;

import org.apache.commons.cli.Options;
import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.Level;

import eu.internetofus.common.vertx.AbstractMain;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Tests over the classes that extends the {@link AbstractMain}
 *
 * @param <T> type of class to test.
 *
 * @see AbstractMain
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public abstract class AbstractMainTestCase<T extends AbstractMain> {

	/**
	 * Create an instance of the main class to test.
	 *
	 * @return an instance of the main class to use for testing.
	 */
	protected abstract T createMain();

	/**
	 * Verify the options are localized.
	 *
	 * @param lang language to load the options.
	 */
	@ParameterizedTest(name = "Should create options for locale {0}")
	@ValueSource(strings = { "en", "es", "ca" })
	public void shouldCreateOptionForLocale(String lang) {

		final Locale locale = Locale.getDefault();
		try {

			final Locale newLocale = new Locale(lang);
			Locale.setDefault(newLocale);
			final T main = this.createMain();
			final Options options = main.createOptions();
			assertThat(options.hasOption(AbstractMain.HELP_OPTION)).isTrue();
			assertThat(options.hasOption(AbstractMain.VERSION_OPTION)).isTrue();
			assertThat(options.hasOption(AbstractMain.CONF_DIR_OPTION)).isTrue();
			assertThat(options.hasOption(AbstractMain.PROPERTY_OPTION)).isTrue();

		} finally {

			Locale.setDefault(locale);
		}
	}

	/**
	 * Verify show help message.
	 *
	 * @param testContext test context over the event bus.
	 * @param stream      captured system output stream.
	 */
	@ExtendWith(SystemOutGuard.class)
	@Test
	public void shouldShowHelpMessage(VertxTestContext testContext, final Capturable stream) {

		stream.capture();
		final T main = this.createMain();
		main.startWith("-" + AbstractMain.HELP_OPTION)
				.onComplete(testContext.succeeding(context -> testContext.verify(() -> {

					final String data = stream.getCapturedData();
					assertThat(data).contains("-" + AbstractMain.HELP_OPTION, "-" + AbstractMain.VERSION_OPTION,
							"-" + AbstractMain.CONF_DIR_OPTION, "-" + AbstractMain.PROPERTY_OPTION);
					testContext.completeNow();

				})));

	}

	/**
	 * Verify show version.
	 *
	 * @param testContext test context over the event bus.
	 * @param stream      captured system err stream.
	 */
	@ExtendWith(SystemErrGuard.class)
	@Test
	public void shouldShowVersion(VertxTestContext testContext, final Capturable stream) {

		stream.capture();
		final T main = this.createMain();
		main.startWith("-" + AbstractMain.VERSION_OPTION)
				.onComplete(testContext.succeeding(context -> testContext.verify(() -> {

					final String data = stream.getCapturedData();
					assertThat(data).contains(Level.INFO.name());
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
	public void shouldCaptureUndefinedArgument(VertxTestContext testContext, final Capturable stream) {

		stream.capture();
		final T main = this.createMain();
		main.startWith("-undefined").onComplete(testContext.failing(error -> testContext.verify(() -> {

			final String data = stream.getCapturedData();
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
	public void shouldCaptureBadPropertyArgument(VertxTestContext testContext, final Capturable stream) {

		stream.capture();
		final T main = this.createMain();
		main.startWith("-" + AbstractMain.PROPERTY_OPTION, "propertyName")
				.onComplete(testContext.failing(error -> testContext.verify(() -> {

					final String data = stream.getCapturedData();
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
	public void shouldCaptureBadConfDirArgument(VertxTestContext testContext, final Capturable stream) {

		stream.capture();
		final T main = this.createMain();
		main.startWith("-" + AbstractMain.CONF_DIR_OPTION)
				.onComplete(testContext.failing(error -> testContext.verify(() -> {

					final String data = stream.getCapturedData();
					assertThat(data).contains(Level.ERROR.name(), Level.INFO.name());
					testContext.completeNow();

				})));

	}

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
	public void shouldNotStartServerBecausePortIsBidded(VertxTestContext testContext, @TempDir File tmpDir)
			throws Throwable {

		final Socket socket = new Socket();
		socket.bind(new InetSocketAddress("localhost", 0));
		final int port = socket.getLocalPort();

		final File confDir = new File(tmpDir, "etc");
		confDir.mkdirs();
		Files.writeString(new File(confDir, "host.json").toPath(),
				"{\"api\":{\"host\":\"localhost\",\"port\":" + port + "}}");

		final T main = this.createMain();
		main.startWith("-" + AbstractMain.CONF_DIR_OPTION, confDir.getAbsolutePath())
				.onComplete(testContext.failing(error -> testContext.verify(() -> {

					socket.close();
					assertThat(error).isNotNull();
					testContext.completeNow();

				})));

	}

	/**
	 * Called when has to fail to start the WeNet module with the specified
	 * arguments.
	 *
	 * @param testContext test context over the event bus.
	 * @param args        to start the weNet module.
	 */
	protected void assertNotStartWith(VertxTestContext testContext, String... args) {

		final T main = this.createMain();
		main.startWith(args).onComplete(testContext.failing(error -> testContext.verify(() -> {

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
	public void shouldNotStartServerBecauseConfigurationFilesAreWrong(VertxTestContext testContext,
			final Capturable stream, @TempDir File tmpDir) throws Throwable {

		final File confDir = new File(tmpDir, "etc");
		confDir.mkdirs();
		new File(confDir, "Z").mkdirs();
		final File unreadable = new File(confDir, "x.json");
		unreadable.createNewFile();
		unreadable.setReadable(false);
		Files.writeString(new File(confDir, "bad_yaml.yml").toPath(), "{\"api\":{\"port\":0}}");
		Files.writeString(new File(confDir, "bad_json.json").toPath(), "port:0");

		this.assertNotStartWith(testContext, "-" + AbstractMain.CONF_DIR_OPTION, confDir.getAbsolutePath());

	}

	/**
	 * Verify capture exception when configure the configuration directory.
	 *
	 * @param arguments   that can not be used to start the WeNet Module.
	 * @param testContext test context over the event bus.
	 */
	@ParameterizedTest(name = "Should not start with the arguments: {0}")
	@ValueSource(
			strings = {
					"-" + AbstractMain.PROPERTY_OPTION + "api.host=\"localhost\",-" + AbstractMain.PROPERTY_OPTION
							+ "api.port=\"80\"",
					"-" + AbstractMain.CONF_DIR_OPTION + "undefined://bad/path/to/conf/dir",
					"-" + AbstractMain.PROPERTY_OPTION + ",persistence.connection_string=\"undefined connection value\"",
					"-" + AbstractMain.PROPERTY_OPTION + ",webClient.keepAlive=false,-" + AbstractMain.PROPERTY_OPTION
							+ ", webClient.pipelining=true" })
	public void shouldNotStartWithBadBasicArguments(String arguments, VertxTestContext testContext) {

		final String[] args = arguments.split(",");
		this.assertNotStartWith(testContext, args);

	}

	/**
	 * Check configuration load from properties.
	 *
	 * @param testContext test context over the event bus.
	 *
	 * @throws Throwable if can not create the temporal files.
	 */
	@Test
	@ExtendWith(VertxExtension.class)
	public void shouldLoadConfigurationProperties(VertxTestContext testContext) throws Throwable {

		final T main = this.createMain();
		testContext.assertComplete(main.startWith("-" + AbstractMain.PROPERTY_OPTION + "api.host=\"HOST\"",
				"-" + AbstractMain.PROPERTY_OPTION + "api.port=80", "-" + AbstractMain.PROPERTY_OPTION,
				"persistence.db_name=profile-manager", "-" + AbstractMain.PROPERTY_OPTION, "persistence.username=db-user-name",
				"-" + AbstractMain.PROPERTY_OPTION + " persistence.host=phost",
				"-" + AbstractMain.PROPERTY_OPTION + "persistence.port=27", "-" + AbstractMain.PROPERTY_OPTION,
				"persistence.db_name=DB_NAME", "-" + AbstractMain.PROPERTY_OPTION + "persistence.username=USER_NAME",
				"-" + AbstractMain.PROPERTY_OPTION + " persistence.password=PASSWORD", "-" + AbstractMain.PROPERTY_OPTION,
				"webClient.keepAlive=false", "-" + AbstractMain.PROPERTY_OPTION, "webClient.pipelining=true",
				"-" + AbstractMain.VERSION_OPTION)).onComplete(handler -> {

					final ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), main.retrieveOptions);
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
						assertThat(conf.getJsonObject("wenetComponents")).isNotNull();
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
	@ExtendWith(VertxExtension.class)
	public void shouldLoadConfigurationFromFiles(VertxTestContext testContext, @TempDir File tmpDir) throws Throwable {

		final File etc = new File(tmpDir, "etc");
		etc.mkdirs();
		final JsonObject api = new JsonObject().put("host", "HOST").put("port", 80);
		final File apiFile = new File(etc, "api.json");
		apiFile.createNewFile();
		Files.writeString(apiFile.toPath(), new JsonObject().put("api", api).encodePrettily());
		final StringBuilder persistence = new StringBuilder();
		persistence.append("persistence:\n");
		persistence.append("  host: phost\n");
		persistence.append("  port: 27\n");
		persistence.append("  db_name: \"DB_NAME\"\n");
		persistence.append("  username: USER_NAME\n");
		persistence.append("  password: \"PASSWORD\"\n");
		final File persistenceFile = new File(etc, "persistence.yml");
		persistenceFile.createNewFile();
		Files.writeString(persistenceFile.toPath(), persistence.toString());

		final T main = this.createMain();
		testContext.assertComplete(
				main.startWith("-" + AbstractMain.CONF_DIR_OPTION, etc.getAbsolutePath(), "-" + AbstractMain.VERSION_OPTION))
				.onComplete(handler -> {

					final ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), main.retrieveOptions);
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
	 * Check configuration properties are preferred to the defined on the
	 * configuration files.
	 *
	 * @param testContext test context over the event bus.
	 * @param tmpDir      temporal directory.
	 *
	 * @throws Throwable if can not create the temporal files.
	 */
	@Test
	@ExtendWith(VertxExtension.class)
	public void shouldConfigureAndUsePropertiesBeforeFiles(VertxTestContext testContext, @TempDir File tmpDir)
			throws Throwable {

		final File etc = new File(tmpDir, "etc");
		etc.mkdirs();
		final JsonObject api = new JsonObject().put("host", "HOST").put("port", 80);
		final File apiFile = new File(etc, "api.json");
		apiFile.createNewFile();
		Files.writeString(apiFile.toPath(), new JsonObject().put("api", api).encodePrettily());
		final StringBuilder persistence = new StringBuilder();
		persistence.append("persistence:\n");
		persistence.append("  host: phost\n");
		persistence.append("  port: 27\n");
		persistence.append("  db_name: \"DB_NAME\"\n");
		persistence.append("  username: USER_NAME\n");
		persistence.append("  password: \"PASSWORD\"\n");
		final File persistenceFile = new File(etc, "persistence.yml");
		persistenceFile.createNewFile();
		Files.writeString(persistenceFile.toPath(), persistence.toString());

		final T main = this.createMain();
		testContext
				.assertComplete(main.startWith("-" + AbstractMain.CONF_DIR_OPTION + etc.getAbsolutePath(),
						"-" + AbstractMain.PROPERTY_OPTION + "api.port=8081",
						"-" + AbstractMain.PROPERTY_OPTION + " persistence.db_name=\"database name\"",
						"-" + AbstractMain.PROPERTY_OPTION, "persistence.password=PASSW0RD", "-" + AbstractMain.VERSION_OPTION))
				.onComplete(handler -> {

					final ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), main.retrieveOptions);
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

}
