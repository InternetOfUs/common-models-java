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
 * Test the {@link Routine}
 *
 * @see Routine
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(VertxExtension.class)
public class RoutineTest extends ModelTestCase<Routine> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Routine createModelExample(int index) {

		final Routine routine = new Routine();
		routine.id = null;
		routine.label = "label_" + index;
		routine.proximity = "proximity_" + index;
		routine.from_time = "18:00:0" + index % 10;
		routine.to_time = "22:00:0" + index % 10;
		return routine;
	}

	/**
	 * Check that the {@link #createModelExample(int)} is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldExampleBeValid(Vertx vertx, VertxTestContext testContext) {

		final Routine model = this.createModelExample(1);
		assertIsValid(model, vertx, testContext);
	}

	/**
	 * Check that a model with all the values is valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldFullModelBeValid(Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.id = "      ";
		model.label = "    label    ";
		model.proximity = "    proximity   ";
		model.from_time = "   18:19    ";
		model.to_time = "  22:21:20   ";
		assertIsValid(model, vertx, testContext, () -> {

			final Routine expected = new Routine();
			expected.id = model.id;
			expected.label = "label";
			expected.proximity = "proximity";
			expected.from_time = "18:19:00";
			expected.to_time = "22:21:20";
			assertThat(model).isEqualTo(expected);

		});
	}

	/**
	 * Check that the model with id is not valid.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithAnId(Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.id = "has_id";
		assertIsNotValid(model, "id", vertx, testContext);

	}

	/**
	 * Check that not accept routines with bad label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadLabel(Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.label = ValidationsTest.STRING_256;
		assertIsNotValid(model, "label", vertx, testContext);

	}

	/**
	 * Check that not accept routines with bad proximity.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@Test
	public void shouldNotBeValidWithABadProximity(Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.proximity = ValidationsTest.STRING_256;
		assertIsNotValid(model, "proximity", vertx, testContext);

	}

	/**
	 * Check that not accept routines with bad from_time.
	 *
	 * @param badTime     a bad time value.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with from_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotBeValidWithABadFrom_time(String badTime, Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.from_time = badTime;
		assertIsNotValid(model, "from_time", vertx, testContext);

	}

	/**
	 * Check that not accept routines with bad to_time.
	 *
	 * @param badTime     a bad time value.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#validate(String, io.vertx.core.Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with to_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotBeValidWithABadTo_time(String badTime, Vertx vertx, VertxTestContext testContext) {

		final Routine model = new Routine();
		model.to_time = badTime;
		assertIsNotValid(model, "to_time", vertx, testContext);

	}

	/**
	 * Check that not merge routines with bad label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadLabel(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		final Routine source = new Routine();
		source.label = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "label", vertx, testContext);

	}

	/**
	 * Check that not merge routines with bad proximity.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldNotMergeWithABadProximity(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		final Routine source = new Routine();
		source.proximity = ValidationsTest.STRING_256;
		assertCannotMerge(target, source, "proximity", vertx, testContext);

	}

	/**
	 * Check that not merge routines with bad from_time.
	 *
	 * @param badTime     a bad time value.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with from_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotMergeWithABadFrom_time(String badTime, Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		final Routine source = new Routine();
		source.from_time = badTime;
		assertCannotMerge(target, source, "from_time", vertx, testContext);

	}

	/**
	 * Check that not merge routines with bad to_time.
	 *
	 * @param badTime     a bad time value.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@ParameterizedTest(name = "Should not be valid with to_time = {0}")
	@ValueSource(strings = { "0", "tomorrow", "2019-23-10", "10:0", "2019-02-02T00:00:00Z" })
	public void shouldNotMergeWithABadTo_time(String badTime, Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		final Routine source = new Routine();
		source.to_time = badTime;
		assertCannotMerge(target, source, "to_time", vertx, testContext);

	}

	/**
	 * Check that merge.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMerge(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		target.id = "1";
		final Routine source = this.createModelExample(2);
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			source.id = "1";
			assertThat(merged).isEqualTo(source);
		});
	}

	/**
	 * Check that merge with {@code null}.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMergeWithNull(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		assertCanMerge(target, null, vertx, testContext, merged -> assertThat(merged).isSameAs(target));
	}

	/**
	 * Check that merge only label.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyLabel(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		target.id = "1";
		final Routine source = new Routine();
		source.label = "NEW LABEL";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.label = "NEW LABEL";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only proximity.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyProximity(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		target.id = "1";
		final Routine source = new Routine();
		source.proximity = "NEW PROXIMITY";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.proximity = "NEW PROXIMITY";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only from_time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyFrom_time(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		target.id = "1";
		final Routine source = new Routine();
		source.from_time = "00:00";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.from_time = "00:00:00";
			assertThat(merged).isEqualTo(target);
		});
	}

	/**
	 * Check that merge only to_time.
	 *
	 * @param vertx       event bus to use.
	 * @param testContext test context to use.
	 *
	 * @see Routine#merge(Routine, String, Vertx)
	 */
	@Test
	public void shouldMergeOnlyTo_time(Vertx vertx, VertxTestContext testContext) {

		final Routine target = this.createModelExample(1);
		target.id = "1";
		final Routine source = new Routine();
		source.to_time = "00:00";
		assertCanMerge(target, source, vertx, testContext, merged -> {
			assertThat(merged).isNotEqualTo(target).isNotEqualTo(source);
			target.to_time = "00:00:00";
			assertThat(merged).isEqualTo(target);
		});
	}

}
