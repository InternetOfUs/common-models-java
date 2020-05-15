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

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.components.ModelTestCase;
import eu.internetofus.common.components.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link TaskTransactionType}.
 *
 * @see TaskTransactionType
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class TaskTransactionTypeTest extends ModelTestCase<TaskTransactionType> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskTransactionType createModelExample(int index) {

		final TaskTransactionType model = new TaskTransactionType();
		model.label = "Label_" + index;
		model.description = "Description_" + index;
		model.attributes = new ArrayList<>();
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(index));
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
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the model is not valid without label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithoutLabel(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.label = null;
		assertIsNotValid(model, "label", vertx, testContext);

	}

	/**
	 * Check that the model is not valid if has a large label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeLabel(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.label = ValidationsTest.STRING_256;
		assertIsNotValid(model, "label", vertx, testContext);

	}

	/**
	 * Check that the model is not valid if has a large label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidALabelWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.label = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.label).isEqualTo("1234567890"));

	}

	/**
	 * Check that the model is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.description = ValidationsTest.STRING_1024;
		assertIsNotValid(model, "description", vertx, testContext);

	}

	/**
	 * Check that the model is not valid if has a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidADescriptionWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.description = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.description).isEqualTo("1234567890"));

	}

	/**
	 * Check that the model is not valid with a bad attribute.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.attributes = new ArrayList<>();
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		model.attributes.add(new TaskAttributeType());
		assertIsNotValid(model, "attributes[2].name", vertx, testContext);

	}

	/**
	 * Check that the model is not valid with a duplicated attribute label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext context to test.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithDuplicatedAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType model = this.createModelExample(1);
		model.attributes = new ArrayList<>();
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		model.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		model.attributes.get(2).name = model.attributes.get(0).name;
		assertIsNotValid(model, "attributes[2]", vertx, testContext);

	}

	/**
	 * Check that two {@link #createModelExample(int)} can be merged.
	 *
	 * @param index       to verify
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#merge(TaskTransactionType, String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be merged")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExamplesBeMerged(int index, Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType source = this.createModelExample(index);
		final TaskTransactionType target = this.createModelExample(index + 1);
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

	}

	/**
	 * Check that cannot merge with a large label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldCannotMergeALargeLabel(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.label = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "label", vertx, testContext);

	}

	/**
	 * Check can merge only the label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyLabel(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.label = "NEW LABEL";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.label = "NEW LABEL";
			assertThat(merged).isEqualTo(target);
		});

	}

	/**
	 * Check that cannot merge with a large description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldCannotMergeALargeDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.description = ValidationsTest.STRING_1024;
		assertCannotMerge(target, source, "description", vertx, testContext);

	}

	/**
	 * Check that cane merge only the description.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyDescription(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.description = "NEW DESCRIPTION";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.description = "NEW DESCRIPTION";
			assertThat(merged).isEqualTo(target);
		});

	}

	/**
	 * Check that cannot merge with a bad attribute.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldCannotMergeABadAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		source.attributes.add(new TaskAttributeType());
		assertCannotMerge(target, source, "attributes[2].name", vertx, testContext);

	}

	/**
	 * Check that cannot merge with a duplicated attribute.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldCannotMergeWithDuplicatedAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		source.attributes.get(2).name = source.attributes.get(0).name;
		assertCannotMerge(target, source, "attributes[2]", vertx, testContext);

	}

	/**
	 * Check can merge an attribute.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldMergeAnAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		final TaskTransactionType source = new TaskTransactionType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		source.attributes.get(0).description = "NEW DESCRIPTION";
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.attributes.get(0).description = "NEW DESCRIPTION";
			assertThat(merged).isEqualTo(target);
		});

	}

	/**
	 * Check can merge removing attributes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldMergeRemovingAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		target.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		target.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		final TaskTransactionType source = new TaskTransactionType();
		source.attributes = new ArrayList<>();
		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.attributes.clear();
			assertThat(merged).isEqualTo(target);
		});

	}

	/**
	 * Check can merge some attributes.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see TaskTransactionType#validate(String, Vertx)
	 */
	@Test
	public void shouldMergeMixingAttribute(Vertx vertx, VertxTestContext testContext) {

		final TaskTransactionType target = this.createModelExample(1);
		target.attributes.add(new TaskAttributeTypeTest().createModelExample(2));
		target.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		final TaskTransactionType source = new TaskTransactionType();
		source.attributes = new ArrayList<>();
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(3));
		source.attributes.add(new TaskAttributeTypeTest().createModelExample(1));
		source.attributes.get(0).description = "NEW DESCRIPTION";

		assertCanMerge(target, source, vertx, testContext, merged -> {

			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.attributes.remove(1);
			target.attributes.add(target.attributes.remove(0));
			target.attributes.get(0).description = "NEW DESCRIPTION";
			assertThat(merged).isEqualTo(target);
		});

	}

}
