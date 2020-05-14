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

import java.time.LocalDate;

import eu.internetofus.common.api.models.ValidationErrorException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A birth date of an alive person.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class AliveBirthDate extends ProfileDate {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		return super.validate(codePrefix, vertx).compose(mapper -> {

			final Promise<Void> promise = Promise.promise();
			if (this.year == null || this.month == null || this.day == null) {

				promise.complete();

			} else {

				final LocalDate birthDate = LocalDate.of(this.year, this.month, this.day);
				if (birthDate.isAfter(LocalDate.now())) {

					promise.fail(new ValidationErrorException(codePrefix, "The birth date can not be on the future"));

				} else if (birthDate.isBefore(LocalDate.of(1903, 1, 2))) {

					promise.fail(new ValidationErrorException(codePrefix,
							"The user can not be born before Kane Tanake, the oldest living person on earth"));

				} else {

					promise.complete();
				}
			}
			return promise.future();

		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<ProfileDate> merge(ProfileDate source, String codePrefix, Vertx vertx) {

		return super.merge(source, codePrefix, vertx).compose(model -> {
			final AliveBirthDate date = new AliveBirthDate();
			date.day = model.day;
			date.month = model.month;
			date.year = model.year;
			return date.validate(codePrefix, vertx).map(empty -> date);
		});
	}

}
