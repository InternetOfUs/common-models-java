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

package eu.internetofus.common.components.task_manager;

import static eu.internetofus.common.components.MergesTest.assertCanMerge;
import static eu.internetofus.common.components.MergesTest.assertCannotMerge;
import static eu.internetofus.common.components.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.components.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import eu.internetofus.common.components.task_manager.TaskAttribute;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TaskAttribute}.
 *
 * @see TaskAttribute
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskAttributeTest extends ModelTestCase<TaskAttribute> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskAttribute createModelExample(int index) {

		final TaskAttribute model = new TaskAttribute();
		model.name = "name_" + index;
		model.value = "value_" + index;
		return model;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute model = new TaskAttribute();
		model.name = ValidationsTest.STRING_256;
		assertIsNotValid(model, "name", vertx, testContext);

	}

	/**
	 * Check that the attribute is not valid without a name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithoutAName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute model = new TaskAttribute();
		model.value = ValidationsTest.STRING_256;
		assertIsNotValid(model, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute model = new TaskAttribute();
		model.name = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = new TaskAttribute();
		final TaskAttribute source = new TaskAttribute();
		source.name = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "name", vertx, testContext);

	}

	/**
	 * Check that can not merge is a name is not defined.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithoutAName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = new TaskAttribute();
		final TaskAttribute source = new TaskAttribute();
		source.value = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMergeANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = new TaskAttribute();
		final TaskAttribute source = new TaskAttribute();
		source.name = "   1234567890   ";
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = this.createModelExample(1);
		final TaskAttribute source = this.createModelExample(23);
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that can merge with an empty model.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMergeWithEmptyModel(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = this.createModelExample(1);
		assertCanMerge(target, new TaskAttribute(), vertx, testContext, merged -> assertThat(merged).isEqualTo(target));

	}

	/**
	 * Check that merge only the name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = this.createModelExample(1);
		final TaskAttribute source = new TaskAttribute();
		source.name = "NEW NAME";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.name = "NEW NAME";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the value.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttribute#merge(TaskAttribute, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyValue(Vertx vertx, VertxTestContext testContext) {

		final TaskAttribute target = this.createModelExample(1);
		final TaskAttribute source = new TaskAttribute();
		source.value = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.value = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

}
