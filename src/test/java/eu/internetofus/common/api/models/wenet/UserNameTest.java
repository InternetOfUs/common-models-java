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

package eu.internetofus.common.api.models.wenet;

import static eu.internetofus.common.api.models.MergesTest.assertCanMerge;
import static eu.internetofus.common.api.models.MergesTest.assertCannotMerge;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsNotValid;
import static eu.internetofus.common.api.models.ValidationsTest.assertIsValid;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import eu.internetofus.common.api.models.ModelTestCase;
import eu.internetofus.common.api.models.ValidationsTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * Test the {@link UserName}.
 *
 * @see UserName
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class UserNameTest extends ModelTestCase<UserName> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserName createModelExample(int index) {

		final UserName name = new UserName();
		name.prefix = "prefix_" + index;
		name.first = "first_" + index;
		name.middle = "middle_" + index;
		name.last = "last_" + index;
		name.suffix = "suffix_" + index;
		return name;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param index       to verify
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@ParameterizedTest(name = "The model example {0} has to be valid")
	@ValueSource(ints = { 0, 1, 2, 3, 4, 5 })
	public void shouldExampleBeValid(int index, Vertx vertx, VertxTestContext testContext) {

		final UserName model = this.createModelExample(index);
		assertIsValid(model, vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large prefix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargePrefix(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.prefix = "12345678901";
		assertIsNotValid(model, "prefix", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large prefix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidAPrefixWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.prefix = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.prefix).isEqualTo("1234567890"));

	}

	/**
	 * Check that the name is not valid if has a large first.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeFirst(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.first = ValidationsTest.STRING_256;
		assertIsNotValid(model, "first", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large first.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidAFirstWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.first = "   First name 1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.first).isEqualTo("First name 1234567890"));

	}

	/**
	 * Check that the name is not valid if has a large middle.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeMiddle(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.middle = ValidationsTest.STRING_256;
		assertIsNotValid(model, "middle", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large middle.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidAMiddleWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.middle = "   Middle name 1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.middle).isEqualTo("Middle name 1234567890"));

	}

	/**
	 * Check that the name is not valid if has a large last.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeLast(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.last = ValidationsTest.STRING_256;
		assertIsNotValid(model, "last", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large last.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidALastWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.last = "   Last name 1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.last).isEqualTo("Last name 1234567890"));

	}

	/**
	 * Check that the name is not valid if has a large suffix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldNotBeValidWithALargeSuffix(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.suffix = "12345678901";
		assertIsNotValid(model, "suffix", vertx, testContext);

	}

	/**
	 * Check that the name is not valid if has a large suffix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#validate(String, Vertx)
	 */
	@Test
	public void shouldBeValidASuffixWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName model = new UserName();
		model.suffix = "   1234567890   ";
		assertIsValid(model, vertx, testContext, () -> assertThat(model.suffix).isEqualTo("1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large prefix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargePrefix(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.prefix = "12345678901";
		assertCannotMerge(target, source, "prefix", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large prefix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeAPrefixWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.prefix = "   1234567890   ";
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.prefix).isEqualTo("1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large first.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeFirst(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.first = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "first", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large first.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeAFirstWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.first = "   First name 1234567890   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.first).isEqualTo("First name 1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large middle.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeMiddle(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.middle = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "middle", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large middle.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeAMiddleWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.middle = "   Middle name 1234567890   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.middle).isEqualTo("Middle name 1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large last.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeLast(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.last = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "last", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large last.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeALastWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.last = "   Last name 1234567890   ";
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged.last).isEqualTo("Last name 1234567890"));

	}

	/**
	 * Check that the name is not merge if has a large suffix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithALargeSuffix(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.suffix = "12345678901";
		assertCannotMerge(target, source, "suffix", vertx, testContext);

	}

	/**
	 * Check that the name is not merge if has a large suffix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeASuffixWithSpaces(Vertx vertx, VertxTestContext testContext) {

		final UserName target = new UserName();
		final UserName source = new UserName();
		source.suffix = "   1234567890   ";
		assertCanMerge(target, source, vertx, testContext, merged -> assertThat(merged.suffix).isEqualTo("1234567890"));

	}

	/**
	 * Check that merge two models.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = this.createModelExample(23);
		assertCanMerge(target, source, vertx, testContext,
				merged -> assertThat(merged).isNotEqualTo(target).isEqualTo(source));

	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));

	}

	/**
	 * Check that merge only the prefix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyPrefix(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.prefix = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.prefix = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the first.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyFirst(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.first = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.first = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the middle.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyMiddle(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.middle = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.middle = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the last.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyLast(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.last = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.last = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only the suffix.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see UserName#merge(UserName, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlySuffix(Vertx vertx, VertxTestContext testContext) {

		final UserName target = this.createModelExample(1);
		final UserName source = new UserName();
		source.suffix = "NEW VALUE";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.suffix = "NEW VALUE";
			assertThat(merged).isEqualTo(target);
		});
	}

}
