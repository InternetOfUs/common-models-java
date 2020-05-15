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
import eu.internetofus.common.components.task_manager.TaskAttributeType;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TaskAttributeType}.
 *
 * @see TaskAttributeType
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskAttributeTypeTest extends ModelTestCase<TaskAttributeType> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskAttributeType createModelExample(int index) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = "name_" + index;
		model.description = "description_" + index;
		model.type = TaskAttributeType.TYPE_NAMES[index % (TaskAttributeType.TYPE_NAMES.length - 1)];
		model.isArray = index % 2 == 0;
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
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = ValidationsTest.STRING_256;
		assertIsNotValid(model, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.type = TaskAttributeType.BOOLEAN_TYPE_NAME;
		model.name = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that the description is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = "communityId";
		model.description = ValidationsTest.STRING_1024;
		model.type = TaskAttributeType.STRING_TYPE_NAME;
		assertIsNotValid(model, "description", vertx, testContext);

	}

	/**
	 * Check that the description is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidADescriptionWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = "communityId";
		model.description = "   " + ValidationsTest.STRING_256 + "   ";
		model.type = TaskAttributeType.STRING_TYPE_NAME;
		assertIsValid(model, vertx, testContext, () -> assertThat(model.description).isEqualTo(ValidationsTest.STRING_256));

	}

	/**
	 * Check that the type is not valid if has an undefined type name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnundefinedType(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = "communityId";
		model.type = "undefined type name";
		assertIsNotValid(model, "type", vertx, testContext);

	}

	/**
	 * Check that the type is not valid if has a large type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidATypeWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType model = new TaskAttributeType();
		model.name = "communityId";
		model.type = "   " + TaskAttributeType.NUMBER_TYPE_NAME + "   ";
		assertIsValid(model, vertx, testContext,
				() -> assertThat(model.type).isEqualTo(TaskAttributeType.NUMBER_TYPE_NAME));

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		final TaskAttributeType source = new TaskAttributeType();
		source.name = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "name", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeANameWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		target.type = TaskAttributeType.BOOLEAN_TYPE_NAME;
		final TaskAttributeType source = new TaskAttributeType();
		source.name = "   1234567890   ";
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.name).isEqualTo("1234567890"));

	}

	/**
	 * Check that the description is not merge if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		target.name = "name";
		target.type = TaskAttributeType.BOOLEAN_TYPE_NAME;
		final TaskAttributeType source = new TaskAttributeType();
		source.description = ValidationsTest.STRING_1024;
		assertCannotMerge(target, source, "description", vertx, testContext);

	}

	/**
	 * Check that the description is not merge if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeADescriptionWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		target.name = "name";
		target.type = TaskAttributeType.BOOLEAN_TYPE_NAME;
		final TaskAttributeType source = new TaskAttributeType();
		source.description = "   " + ValidationsTest.STRING_256 + "   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.description).isEqualTo(ValidationsTest.STRING_256));

	}

	/**
	 * Check that the type is not merge if has an undefined type name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeType(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		target.name = "name";
		final TaskAttributeType source = new TaskAttributeType();
		source.type = "undefined type";
		assertCannotMerge(target, source, "type", vertx, testContext);

	}

	/**
	 * Check that the type is not merge if has a large type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeATypeWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = new TaskAttributeType();
		target.name = "name";
		target.type = TaskAttributeType.BOOLEAN_TYPE_NAME;
		final TaskAttributeType source = new TaskAttributeType();
		source.type = "   " + TaskAttributeType.NUMBER_TYPE_NAME + "   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.type).isEqualTo(TaskAttributeType.NUMBER_TYPE_NAME));

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = this.createModelExample(1);
		final TaskAttributeType source = this.createModelExample(23);
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that merge only the name.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyName(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = this.createModelExample(1);
		final TaskAttributeType source = new TaskAttributeType();
		source.name = "NEW NAME";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.name = "NEW NAME";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = this.createModelExample(1);
		final TaskAttributeType source = new TaskAttributeType();
		source.description = "NEW DESCRIPTION";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.description = "NEW DESCRIPTION";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the type.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskAttributeType#merge(TaskAttributeType, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyType(Vertx vertx, VertxTestContext testContext) {

		final TaskAttributeType target = this.createModelExample(1);
		final TaskAttributeType source = new TaskAttributeType();
		source.type = TaskAttributeType.PROFILE_ID_TYPE_NAME;
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.type = TaskAttributeType.PROFILE_ID_TYPE_NAME;
			assertThat(merged).isEqualTo(target);
		});
	}

}
