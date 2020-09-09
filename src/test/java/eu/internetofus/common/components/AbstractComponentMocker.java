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

package eu.internetofus.common.components;

import java.net.InetAddress;
import java.util.Map;

import com.intuit.karate.Resource;
import com.intuit.karate.core.Feature;
import com.intuit.karate.core.FeatureParser;
import com.intuit.karate.netty.FeatureServer;

import io.vertx.core.json.JsonObject;

/**
 * The generic component used to mock any WeNet component.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractComponentMocker {

  /**
   * The feature server that is mocked.
   */
  protected FeatureServer server;

  /**
   * Return the feature that mock the component. This return the feature defined in the class path with the same name of
   * this class and with the extension {@code '.feature'}.
   *
   * @return the feature to use on the mocked server
   */
  protected Feature createComponentFeature() {

    final var url = this.getClass().getClassLoader().getResource(this.getClass().getName().replaceAll("\\.", "/") + ".feature");
    final var resource = new Resource(url);
    return FeatureParser.parse(resource);

  }

  /**
   * Start a new mocked server.
   *
   * @param port      to bind the mocker server.
   * @param variables to initialize the mocker, or {@code null} to use the default.
   *
   * @see #createComponentFeature()
   */
  public void start(final int port, final Map<String, Object> variables) {

    this.stop();
    final var feature = this.createComponentFeature();
    this.server = new FeatureServer(feature, port, false, variables);
  }

  /**
   * Return the base URL to the API that the mocked service respond.
   *
   * @return the mocker API URL.
   */
  public String getApiUrl() {

    final var builder = new StringBuilder();
    builder.append("http://");
    try {

      final var IP = InetAddress.getLocalHost();
      builder.append(IP.getHostAddress());

    } catch (final Throwable t) {

      builder.append("localhost");
    }

    builder.append(":");
    builder.append(this.getPort());

    return builder.toString();

  }

  /**
   * Stop the started server.
   */
  public void stop() {

    if (this.server != null) {

      this.server.stop();
      this.server = null;
    }

  }

  /**
   * Return the port that the server is bind.
   *
   * @return the port where the server is bind or {@code 0} if the server is not started.
   */
  public int getPort() {

    if (this.server != null) {

      return this.server.getPort();

    }

    return 0;

  }

  /**
   * Return the configuration to use when register the component.
   *
   * @return the configuration to register the component.
   */
  public JsonObject getComponentConfiguration() {

    final var conf = new JsonObject().put(this.getComponentConfigurationName(), "http://localhost:" + this.getPort());
    return conf;
  }

  /**
   * The name for the component on the configuration.
   *
   * @return the name of the component on the configuration.
   */
  protected abstract String getComponentConfigurationName();

}
