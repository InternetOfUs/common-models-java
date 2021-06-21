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

package eu.internetofus.common.components;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link Merges} methods.
 *
 * @see Merges
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class MergesTest {

  /**
   * Check can merge simple values.
   *
   * @see Merges#mergeValues(Object,Object)
   */
  @Test
  public void shoulMergeValues() {

    assertThat(Merges.mergeValues(null, null)).isNull();

    final var source = UUID.randomUUID().toString();
    assertThat(Merges.mergeValues(null, source)).isSameAs(source);
    assertThat(Merges.mergeValues(1l, source)).isSameAs(source);

    final var target = UUID.randomUUID().toString();
    assertThat(Merges.mergeValues(target, null)).isSameAs(target);

    assertThat(Merges.mergeValues(target, source)).isSameAs(source);

  }

  /**
   * Check can merge JSON object values.
   *
   * @see Merges#mergeValues(Object,Object)
   */
  @Test
  public void shoulMergeJsonObjectValues() {

    assertThat(Merges.mergeValues(null, null)).isNull();

    final var source = new JsonObject();
    source.put("source", "source");
    assertThat(Merges.mergeValues(null, source)).isSameAs(source);
    assertThat(Merges.mergeValues(1l, source)).isSameAs(source);

    final var target = new JsonObject();
    target.put("target", "target");
    assertThat(Merges.mergeValues(target, null)).isSameAs(target);

    final var merged = new JsonObject();
    merged.put("target", "target");
    merged.put("source", "source");
    assertThat(Merges.mergeValues(target, source)).isEqualTo(merged).isNotSameAs(target).isNotSameAs(source);

  }

  /**
   * Check can merge JSON array values.
   *
   * @see Merges#mergeValues(Object,Object)
   */
  @Test
  public void shoulMergeJsonArrayValues() {

    assertThat(Merges.mergeValues(null, null)).isNull();

    final var source = new JsonArray();
    source.add("source");
    assertThat(Merges.mergeValues(null, source)).isSameAs(source);
    assertThat(Merges.mergeValues(1l, source)).isSameAs(source);

    final var target = new JsonArray();
    target.add("target");
    assertThat(Merges.mergeValues(target, null)).isSameAs(target);

    final var merged = new JsonArray();
    merged.add("source");
    assertThat(Merges.mergeValues(target, source)).isEqualTo(merged).isNotSameAs(target).isNotSameAs(source);

  }

  /**
   * Check can merge JSON objects.
   *
   * @see Merges#mergeJsonObjects(JsonObject, JsonObject)
   */
  @Test
  public void shoulMergeJsonObjects() {

    assertThat(Merges.mergeJsonObjects(null, null)).isNull();

    final var source = new JsonObject();
    source.put("source", "source");
    source.put("key2", new JsonObject().put("subkey1", "value1").put("subkey2", true).put("subkey3", 1l));
    source.put("key3", 1l);
    source.put("key4", new JsonArray().add(1l).add("source").add(new JsonObject().put("key1", "value")));
    assertThat(Merges.mergeJsonObjects(null, source)).isSameAs(source);

    final var target = new JsonObject();
    target.put("target", "target");
    target.put("key2", new JsonObject().put("subkey1", true).put("subkey2", "value"));
    target.put("key3", true);
    target.put("key4", new JsonArray().add(5l).add(true).add(new JsonObject().put("key1", "value")));
    target.put("key5", "value5");
    assertThat(Merges.mergeJsonObjects(target, null)).isSameAs(target);

    final var merged = new JsonObject();
    merged.put("target", "target");
    merged.put("source", "source");
    merged.put("key2", new JsonObject().put("subkey1", "value1").put("subkey2", true).put("subkey3", 1l));
    merged.put("key3", 1l);
    merged.put("key4", new JsonArray().add(1l).add("source").add(new JsonObject().put("key1", "value")));
    merged.put("key5", "value5");
    assertThat(Merges.mergeJsonObjects(target, source)).isEqualTo(merged).isNotSameAs(target).isNotSameAs(source);

  }

  /**
   * Check can merge JSON arrays.
   *
   * @see Merges#mergeJsonArrays(JsonArray, JsonArray)
   */
  @Test
  public void shoulMergeJsonArrays() {

    assertThat(Merges.mergeJsonArrays(null, null)).isNull();

    final var source = new JsonArray();
    source.add("source");
    source.add(new JsonObject().put("subkey1", "value1").put("subkey2", true));
    source.add(1l);
    source.add(new JsonArray().add(1l).add("source").add(new JsonObject().put("key1", "value")));
    assertThat(Merges.mergeJsonArrays(null, source)).isSameAs(source);

    final var target = new JsonArray();
    assertThat(Merges.mergeJsonArrays(target, source)).isSameAs(source);

    target.add("target");
    target.add(new JsonObject().put("subkey1", true).put("subkey2", "value").put("subkey3", 1l));
    target.add(true);
    target.add(new JsonArray().add(5l).add(true).add(new JsonObject().put("key1", "value")));
    assertThat(Merges.mergeJsonArrays(target, null)).isSameAs(target);

    final var merged = new JsonArray();
    merged.add("source");
    merged.add(new JsonObject().put("subkey1", "value1").put("subkey2", true).put("subkey3", 1l));
    merged.add(1l);
    merged.add(new JsonArray().add(1l).add("source").add(new JsonObject().put("key1", "value")));
    assertThat(Merges.mergeJsonArrays(target, source)).isEqualTo(merged).isNotSameAs(target).isNotSameAs(source);

  }

}
