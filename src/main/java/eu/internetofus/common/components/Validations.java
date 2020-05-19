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

package eu.internetofus.common.components;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Generic methods to validate the common fields of the models.
 *
 * @see ValidationErrorException
 *
 * @author UDT-IA, IIIA-CSIC
 */
public interface Validations {

	/**
	 * Verify a string value that can be null.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param maxSize    maximum size of the string.
	 * @param value      to verify.
	 *
	 * @return the verified value.
	 *
	 * @throws ValidationErrorException If the value is not a valid string.
	 */
	static String validateNullableStringField(String codePrefix, String fieldName, int maxSize, String value)
			throws ValidationErrorException {

		if (value != null) {

			final String trimmedValue = value.trim();
			if (trimmedValue.length() == 0) {

				return null;

			} else if (trimmedValue.length() > maxSize) {

				throw new ValidationErrorException(codePrefix + "." + fieldName,
						"The '" + trimmedValue + "' is too large. The maximum length is '" + maxSize + "'.");

			} else {

				return trimmedValue;
			}

		} else {

			return null;
		}
	}

	/**
	 * Verify a list of string values.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param maxSize    maximum size of the string.
	 * @param values     to verify.
	 *
	 * @return the verified value.
	 *
	 * @throws ValidationErrorException If the value is not a valid list of strings.
	 */
	static List<String> validateNullableListStringField(String codePrefix, String fieldName, int maxSize,
			List<String> values) throws ValidationErrorException {

		if (values == null || values.isEmpty()) {

			return values;

		} else {

			final List<String> validValues = new ArrayList<>();
			final int max = values.size();
			for (int index = 0; index < max; index++) {

				final String value = Validations.validateNullableStringField(codePrefix, fieldName + "[" + index + "]", maxSize,
						values.get(index));
				if (value != null) {

					validValues.add(value);
				}
			}

			return validValues;
		}

	}

	/**
	 * Verify a string value.
	 *
	 * @param codePrefix     the prefix of the code to use for the error message.
	 * @param fieldName      name of the checking field.
	 * @param maxSize        maximum size of the string.
	 * @param value          to verify.
	 * @param possibleValues values that can have the value.
	 *
	 * @return the verified value.
	 *
	 * @throws ValidationErrorException If the value is not a valid string.
	 */
	static String validateStringField(String codePrefix, String fieldName, int maxSize, String value,
			String... possibleValues) throws ValidationErrorException {

		final String trimedValue = validateNullableStringField(codePrefix, fieldName, maxSize, value);
		if (trimedValue == null) {

			throw new ValidationErrorException(codePrefix + "." + fieldName,
					"The '" + fieldName + "' can not be 'null' or contains an empty value.");

		}
		if (possibleValues != null && possibleValues.length > 0) {

			if (!Arrays.stream(possibleValues).anyMatch(element -> trimedValue.equals(element))) {

				throw new ValidationErrorException(codePrefix + "." + fieldName,
						"'" + trimedValue + "' is not a valid value for the field '" + fieldName + "', because it expects any of '"
								+ Arrays.toString(possibleValues) + "'.");

			}
		}

		return trimedValue;
	}

	/**
	 * Verify an email value.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param value      to verify.
	 *
	 * @return the verified email value.
	 *
	 * @throws ValidationErrorException If the value is not a valid email.
	 *
	 * @see #validateNullableStringField(String, String, int, String)
	 */
	static String validateNullableEmailField(String codePrefix, String fieldName, String value)
			throws ValidationErrorException {

		final String trimmedValue = validateNullableStringField(codePrefix, fieldName, 255, value);
		if (trimmedValue != null && !EmailValidator.getInstance().isValid(trimmedValue)) {

			throw new ValidationErrorException(codePrefix + "." + fieldName,
					"The '" + trimmedValue + "' is not a valid email address.");

		}
		return trimmedValue;
	}

	/**
	 * Verify a locale value.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param value      to verify.
	 *
	 * @return the verified locale value.
	 *
	 * @throws ValidationErrorException If the value is not a valid locale.
	 *
	 * @see #validateNullableStringField(String, String, int,String)
	 */
	static String validateNullableLocaleField(String codePrefix, String fieldName, String value)
			throws ValidationErrorException {

		final String validStringValue = validateNullableStringField(codePrefix, fieldName, 50, value);
		if (validStringValue != null) {

			try {

				LocaleUtils.toLocale(validStringValue);

			} catch (final IllegalArgumentException badLocale) {

				throw new ValidationErrorException(codePrefix + "." + fieldName, badLocale);

			}

		}
		return validStringValue;
	}

	/**
	 * Verify a telephone value.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param locale     to use.
	 * @param value      to verify.
	 *
	 * @return the verified telephone value.
	 *
	 * @throws ValidationErrorException If the value is not a valid telephone.
	 *
	 * @see #validateNullableStringField(String, String,int, String)
	 */
	static String validateNullableTelephoneField(String codePrefix, String fieldName, String locale, String value)
			throws ValidationErrorException {

		final String validStringValue = validateNullableStringField(codePrefix, fieldName, 50, value);
		if (validStringValue != null) {

			try {

				final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
				String defaultRegion = Locale.getDefault().getCountry();
				if (locale != null) {

					defaultRegion = new Locale(locale).getCountry();

				}
				final PhoneNumber number = phoneUtil.parse(validStringValue, defaultRegion);
				if (!phoneUtil.isValidNumber(number)) {

					throw new ValidationErrorException(codePrefix + "." + fieldName,
							"The '" + validStringValue + "' is not a valid telephone number");
				}

				return phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL);

			} catch (final ValidationErrorException error) {

				throw error;

			} catch (final Throwable badTelephone) {

				throw new ValidationErrorException(codePrefix + "." + fieldName, badTelephone);

			}

		}
		return validStringValue;
	}

	/**
	 * Verify an URL value.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param value      to verify.
	 *
	 * @return the verified URL value.
	 *
	 * @throws ValidationErrorException If the value is not a valid URL.
	 *
	 * @see #validateNullableStringField(String, String,int, String)
	 */
	static String validateNullableURLField(String codePrefix, String fieldName, String value)
			throws ValidationErrorException {

		String validStringValue = validateNullableStringField(codePrefix, fieldName, 255, value);
		if (validStringValue != null) {

			try {

				final URL url = new URL(value);
				validStringValue = url.toString();

			} catch (final Throwable badURL) {

				throw new ValidationErrorException(codePrefix + "." + fieldName, badURL);

			}

		}
		return validStringValue;
	}

	/**
	 * Verify a date value.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param format     that has to have the date.
	 * @param value      to verify.
	 *
	 * @return the verified date.
	 *
	 * @throws ValidationErrorException If the value is not a valid date.
	 */
	static String validateNullableStringDateField(String codePrefix, String fieldName, DateTimeFormatter format,
			String value) throws ValidationErrorException {

		String validStringValue = validateNullableStringField(codePrefix, fieldName, 255, value);
		if (validStringValue != null) {

			try {

				final TemporalAccessor date = format.parse(validStringValue);
				validStringValue = format.format(date);

			} catch (final Throwable badDate) {

				throw new ValidationErrorException(codePrefix + "." + fieldName, badDate);

			}

		}
		return validStringValue;
	}

	/**
	 * Validate a fields that contains a list of models.
	 *
	 * @param models     to validate.
	 * @param predicate  used to compare if two models are equals, to check de
	 *                   duplicated.
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param vertx      the event bus infrastructure to use.
	 *
	 * @param <T>        type of model to validate.
	 *
	 *
	 * @return the function that can be composed with the future that is validating
	 *         the model of the filed.
	 *
	 * @see ValidationErrorException
	 */
	static <T extends Validable> Function<Void, Future<Void>> validate(List<T> models, BiPredicate<T, T> predicate,
			String codePrefix, Vertx vertx) {

		return mapper -> {
			final Promise<Void> promise = Promise.promise();
			Future<Void> future = promise.future();
			if (models != null) {

				final ListIterator<T> iterator = models.listIterator();
				while (iterator.hasNext()) {

					final T model = iterator.next();
					if (model == null) {

						iterator.remove();

					} else {

						final int index = iterator.previousIndex();
						final String modelPrefix = codePrefix + "[" + index + "]";
						future = future.compose(elementMapper -> model.validate(modelPrefix, vertx));
						future = future.compose(elementMapper -> {

							for (int firstIndex = 0; firstIndex < index; firstIndex++) {

								final T element = models.get(firstIndex);
								if (predicate.test(element, model)) {

									return Future.failedFuture(new ValidationErrorException(modelPrefix,
											"This model is already defined at '" + firstIndex + "'."));

								}
							}

							return Future.succeededFuture();

						});
					}

				}

			}

			promise.complete();

			return future;
		};
	}

	/**
	 * Validate that the value is a valid time stamp.
	 *
	 * @param codePrefix the prefix of the code to use for the error message.
	 * @param fieldName  name of the checking field.
	 * @param value      to verify.
	 * @param nullable   is{@code true} if the value can be {@code null}.
	 *
	 * @return the valid time stamp value.
	 *
	 * @throws ValidationErrorException If the value is not a valid time stamp.
	 */
	static Long validateTimeStamp(String codePrefix, String fieldName, Long value, boolean nullable)
			throws ValidationErrorException {

		if (value != null) {

			if (value < 0) {

				throw new ValidationErrorException(codePrefix + "." + fieldName,
						"The '" + value + "' is not valid time stamp because is less than '0'.");

			}

		} else if (!nullable) {

			throw new ValidationErrorException(codePrefix + "." + fieldName, "The '" + value + "' has to be defined.");

		}
		return value;
	}

}