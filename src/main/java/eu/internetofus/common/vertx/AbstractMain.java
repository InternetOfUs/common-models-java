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

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.tinylog.Level;
import org.tinylog.Logger;
import org.tinylog.jul.JulTinylogBridge;
import org.tinylog.provider.InternalLogger;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * The generic component to start the Module environment.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractMain {

  /**
   * The name of the option to obtain help information.
   */
  public static final String HELP_OPTION = "h";

  /**
   * The name of the option to show the version of the software.
   */
  public static final String VERSION_OPTION = "v";

  /**
   * The name of the option to define a directory where are the configuration files.
   */
  public static final String CONF_DIR_OPTION = "c";

  /**
   * The name of the option to define a property value.
   */
  public static final String PROPERTY_OPTION = "p";

  /**
   * The configurations to start the vertx verticles.
   */
  protected ConfigRetrieverOptions retrieveOptions;

  /**
   * The configuration property that define if has to store the effective configuration.
   */
  public static final String STORE_EFFECTIVE_CONFIGURATION = "store_effective_configuration";

  /**
   * The configuration property that contains the path where the effective configuration has to be stored.
   */
  public static final String EFFECTIVE_CONFIGURATION_PATH = "effective_configuration_path";

  /**
   * The default path where the effective configuration will be stored.
   */
  public static final String DEFAULT_EFFECTIVE_CONFIGURATION_PATH = "var/effective-conf.json";

  /**
   * The maximum milliseconds that the system has to be open. If it is {0} or less the system is available for ever.
   */
  protected long delay;

  /**
   * Create the component to start the server.
   */
  public AbstractMain() {

    this.retrieveOptions = new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("file").setFormat("json").setConfig(new JsonObject().put("path", this.getDefaultModuleConfigurationResurcePath())));
    this.delay = -1l;

  }

  /**
   * Return the resource path to the default configuration file of the module. It is calculated ad the module name plus
   * ".configuration.json".
   *
   * @return the resource path to the default configuration file.
   *
   * @see #getModuleName()
   */
  protected String getDefaultModuleConfigurationResurcePath() {

    final var moduleName = this.getModuleName();
    return moduleName + ".configuration.json";

  }

  /**
   * Load the configuration, start the VertX and deploy the main verticle.
   *
   * @return the promise of the started Vert.x if it can be started.
   */
  public Future<WeNetModuleContext> startVertx() {

    final Promise<WeNetModuleContext> promise = Promise.promise();
    final var vertx = Vertx.vertx();
    final var retriever = ConfigRetriever.create(vertx, this.retrieveOptions);
    retriever.getConfig(confResult -> {

      if (confResult.succeeded()) {

        final var conf = confResult.result();
        vertx.close();

        Logger.info("Loaded configuration: {}", conf);
        if (conf.getBoolean(STORE_EFFECTIVE_CONFIGURATION, Boolean.TRUE)) {
          try {

            final var effectiveConf = FileSystems.getDefault().getPath(conf.getString(EFFECTIVE_CONFIGURATION_PATH, DEFAULT_EFFECTIVE_CONFIGURATION_PATH));
            Files.write(effectiveConf, conf.encodePrettily().getBytes());
            Logger.info("Stored effective configuration at '{}'", effectiveConf);

          } catch (final Throwable throwable) {

            Logger.error(throwable, "Cannot store the effective configuration");
          }

        }

        // Create a new Vert.x instance using the retrieve configuration
        final var options = new VertxOptions(conf);
        final var newVertx = Vertx.vertx(options);

        // Deploy the verticles
        final var deployOptions = new DeploymentOptions().setConfig(conf);
        newVertx.deployVerticle(this.createMainVerticle(), deployOptions, deploy -> {
          if (deploy.succeeded()) {

            promise.complete(new WeNetModuleContext(newVertx, conf));

          } else {

            promise.fail(deploy.cause());
            newVertx.close();
          }
        });

      } else {

        promise.fail(confResult.cause());
      }
    });
    return promise.future();

  }

  /**
   * Create the main verticle that will start the WeNet module components to deploy.
   *
   * @return an instance of the main verticle to deploy.
   */
  protected abstract Verticle createMainVerticle();

  /**
   * Set up the logging system.
   */
  protected void startLoggingSystems() {

    JulTinylogBridge.activate();

  }

  /**
   * Create the options for the command line.
   *
   * @return the options that can be used on the command line.
   */
  protected Options createOptions() {

    final var l10n = ResourceBundle.getBundle(AbstractMain.class.getName().replaceAll("\\.", "/"));
    final var options = new Options();
    options.addOption(HELP_OPTION, l10n.getString(HELP_OPTION + "_large"), false, l10n.getString(HELP_OPTION + "_description"));
    options.addOption(VERSION_OPTION, l10n.getString(VERSION_OPTION + "_large"), false, l10n.getString(VERSION_OPTION + "_description"));
    options.addOption(Option.builder(CONF_DIR_OPTION).longOpt(l10n.getString(CONF_DIR_OPTION + "_large")).numberOfArgs(1).argName(l10n.getString(CONF_DIR_OPTION + "_argName")).desc(l10n.getString(CONF_DIR_OPTION + "_description")).build());
    options.addOption(Option.builder(PROPERTY_OPTION).longOpt(l10n.getString(PROPERTY_OPTION + "_large")).numberOfArgs(2).argName(l10n.getString(PROPERTY_OPTION + "_argName")).valueSeparator()
        .desc(l10n.getString(CONF_DIR_OPTION + "_description")).build());
    return options;

  }

  /**
   * Print the help message.
   *
   * @param options used to create the command parser.
   */
  protected void printHelpMessage(final Options options) {

    final var formatter = new HelpFormatter();
    formatter.printHelp(this.getModuleName(), options);

  }

  /**
   * Get the name for the WeNet module that will be started.
   *
   * @return the module name.
   */
  protected abstract String getModuleName();

  /**
   * Print the software version.
   */
  protected void printVersion() {

    var version = "Unknown";
    try {

      final var currentPackage = this.getClass().getPackage();
      version = currentPackage.getImplementationVersion();

    } catch (final Throwable ignored) {
    }

    InternalLogger.log(Level.INFO, version);

  }

  /**
   * Start the server with the specified arguments.
   *
   * @param args arguments to configure the main process.
   *
   * @return the component that will called when the server has started or not.
   */
  public Future<WeNetModuleContext> startWith(final String... args) {

    this.startLoggingSystems();
    try {

      Logger.debug("Start Main with: {}", () -> Arrays.toString(args));
      final CommandLineParser parser = new DefaultParser();
      final var options = this.createOptions();
      final var cmd = parser.parse(options, args);
      if (cmd.hasOption(CONF_DIR_OPTION)) {

        final var confDirValue = cmd.getOptionValue(CONF_DIR_OPTION);
        this.configureWithFilesAt(confDirValue);
      }
      if (cmd.hasOption(PROPERTY_OPTION)) {

        final var properties = cmd.getOptionProperties(PROPERTY_OPTION);
        this.configureWithPropetyValues(properties);
      }

      if (cmd.hasOption(HELP_OPTION)) {

        this.printHelpMessage(options);
        return Future.succeededFuture();

      } else if (cmd.hasOption(VERSION_OPTION)) {

        this.printVersion();
        return Future.succeededFuture();

      } else {

        return this.startVertx();

      }

    } catch (final Throwable throwable) {

      InternalLogger.log(Level.ERROR, throwable.getLocalizedMessage());
      InternalLogger.log(Level.INFO, "Call with -h to obtain help information");
      return Future.failedFuture(throwable);
    }

  }

  /**
   * Load the configuration form a files on a directory.
   *
   * @param confDirValue directory where are the configuration files.
   *
   * @throws Throwable if exist a problem when load the configuration files.
   */
  protected void configureWithFilesAt(final String confDirValue) throws Throwable {

    final var confPath = Path.of(confDirValue);
    Files.list(confPath).filter(confFilePath -> {

      final var file = confFilePath.toFile();
      return file.isFile() && file.canRead();

    }).sorted((a, b) -> b.getFileName().compareTo(a.getFileName())).forEach(confFilePath -> {

      var format = "json";
      final var fileName = confFilePath.getFileName().toString();
      if (fileName.endsWith("yml")) {

        format = "yaml";
      }
      final var confFileOptions = new ConfigStoreOptions().setType("file").setFormat(format).setConfig(new JsonObject().put("path", confFilePath.toFile().getAbsolutePath()));
      this.retrieveOptions = this.retrieveOptions.addStore(confFileOptions);

    });

  }

  /**
   * Configure using the specified properties.
   *
   * @param properties to configure.
   */
  protected void configureWithPropetyValues(final Properties properties) {

    final var userProperties = new JsonObject();
    for (final String key : properties.stringPropertyNames()) {

      var property = userProperties;
      final var sections = key.split("\\.");
      for (var i = 0; i < sections.length - 1; i++) {

        final var section = sections[i].trim();
        if (property.containsKey(section)) {

          property = property.getJsonObject(section);

        } else {

          final var sectionProperty = new JsonObject();
          property.put(section, sectionProperty);
          property = sectionProperty;

        }

      }

      final var value = properties.getProperty(key).trim();

      if (value.matches("\".*\"")) {

        property.put(sections[sections.length - 1], value.substring(1, value.length() - 1));

      } else if (Boolean.TRUE.toString().equalsIgnoreCase(value) || Boolean.FALSE.toString().equalsIgnoreCase(value)) {

        property.put(sections[sections.length - 1], Boolean.valueOf(value));

      } else {

        try {

          final Number number = Integer.parseInt(value);
          property.put(sections[sections.length - 1], number);

        } catch (final Throwable ignored) {

          property.put(sections[sections.length - 1], value);
        }
      }

    }

    final var userPropertiesConf = new ConfigStoreOptions().setType("json").setConfig(userProperties);
    this.retrieveOptions = this.retrieveOptions.addStore(userPropertiesConf);

  }

}
