/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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
package eu.internetofus.common.vertx;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.time.Duration;
import javax.validation.constraints.NotNull;

/**
 * A client that uses a cache to improve some requests
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ComponentClientWithCache extends ComponentClient {

  /**
   * The default seconds the cache store a value.
   */
  public static final long DEFAULT_CACHE_TIMEOUT = 300;

  /**
   * The name of the configuration property that contains the milliseconds the
   * cache has to store the values.
   */
  public static final String CACHE_TIMEOUT_KEY = "timeout";

  /**
   * The default number of cache entries.
   */
  public static final long DEFAULT_CACHE_SIZE = 10000;

  /**
   * The name of the configuration property that contains the milliseconds the
   * cache has to store the values.
   */
  public static final String CACHE_SIZE_KEY = "size";

  /**
   * The cache of the requests. The key is the request and the value is the
   * response.
   */
  protected Cache<String, String> cache;

  /**
   * Create a new component.
   *
   * @param client     to use.
   * @param conf       configuration to use.
   * @param key        on the configuration with the component URL.
   * @param defaultUrl for the component if not defined on the configuration.
   */
  public ComponentClientWithCache(final WebClient client, final JsonObject conf, final String key,
      final String defaultUrl) {

    super(client, conf.getString(key, defaultUrl));

    final var cacheConf = conf.getJsonObject("cache", new JsonObject());
    final var timeout = cacheConf.getLong(CACHE_TIMEOUT_KEY, DEFAULT_CACHE_TIMEOUT);
    final var size = cacheConf.getLong(CACHE_SIZE_KEY, DEFAULT_CACHE_SIZE);
    this.cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(timeout)).maximumSize(size).build();

  }

  /**
   * Check if the head is defined but first check if it is cached.
   *
   * @param paths to the component to post.
   *
   * @return the future with the response object.
   */
  protected Future<Boolean> headWithCache(@NotNull final Object... paths) {

    final var url = this.createAbsoluteUrlWith(paths);
    final var key = "head:" + url;
    final var value = this.cache.getIfPresent(key);
    if (value != null) {

      return Future.succeededFuture(true);

    } else {

      return this.headWithAbsolute(url).map(found -> {
        if (found) {

          this.cache.put(key, "");
        }
        return found;
      });
    }
  }

}
