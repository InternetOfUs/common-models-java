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

package eu.internetofus.common.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the {@link Merges} methods.
 *
 * @see Merges
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
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

  /**
   * Check that two {@code null} model can be merged.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Merges#mergeField
   */
  @Test
  public void shouldMergeNullSourceAndTargetField(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyComplexModelTest().createModelExample(1);
    Future.succeededFuture(model)
        .compose(Merges.mergeField(null, null, new DummyValidateContext("codePrefix.other"), null))
        .onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

          assertThat(merged).isSameAs(model);
          testContext.completeNow();

        })));
  }

  /**
   * Check that merge {@code null} source into a target model.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Merges#mergeField
   */
  @Test
  public void shouldMergeNullSourceAndNotNullTargetField(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyComplexModelTest().createModelExample(1);
    final var target = new DummyComplexModelTest().createModelExample(2);
    Future.succeededFuture(model)
        .compose(Merges.mergeField(target, null, new DummyValidateContext("codePrefix.other"),
            (mergedModel, field) -> mergedModel.other = field))
        .onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

          assertThat(merged).isSameAs(model);
          assertThat(merged.other).isSameAs(target);
          testContext.completeNow();

        })));
  }

  /**
   * Check that merge source into a {@code null} target model.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Merges#mergeField
   */
  @Test
  public void shouldMergeSourceAndNullTargetField(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyComplexModelTest().createModelExample(1);
    final var source = new DummyComplexModelTest().createModelExample(2);
    Future.succeededFuture(model)
        .compose(Merges.mergeField(null, source, new DummyValidateContext("codePrefix.other"),
            (mergedModel, field) -> mergedModel.other = field))
        .onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

          assertThat(merged).isSameAs(model);
          assertThat(merged.other).isSameAs(source);
          testContext.completeNow();

        })));
  }

  /**
   * Check that merge a field list.
   *
   * @param vertx       event bus to use.
   * @param testContext test context to use.
   *
   * @see Merges#mergeFieldList
   */
  @Test
  public void shouldMergeFieldList(final Vertx vertx, final VertxTestContext testContext) {

    final var model = new DummyComplexModelTest().createModelExample(1);
    final var target = new ArrayList<DummyComplexModel>();
    target.add(new DummyComplexModelTest().createModelExample(2));
    target.add(new DummyComplexModelTest().createModelExample(3));
    target.add(new DummyComplexModelTest().createModelExample(9));
    target.get(2).id = null;
    final var source = new ArrayList<DummyComplexModel>();
    source.add(new DummyComplexModelTest().createModelExample(4));
    source.add(new DummyComplexModelTest().createModelExample(3));
    source.add(new DummyComplexModel());
    source.add(new DummyComplexModelTest().createModelExample(30));
    source.get(1).index = 90;
    source.get(2).id = null;
    Future.succeededFuture(model).compose(Merges.mergeFieldList(source, source, new DummyValidateContext("siblings"),
        sibling -> sibling.id != null, (a, b) -> a.id.equals(b.id), (merged, siblings) -> merged.siblings = siblings))
        .onComplete(testContext.succeeding(merged -> testContext.verify(() -> {

          assertThat(merged).isSameAs(model);
          assertThat(merged.siblings).isEqualTo(source);
          testContext.completeNow();

        })));
  }

}
