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

/**
 * This exception explains an error that happens.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class ErrorException extends Exception {

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The code of the error.
	 */
	protected String code;

	/**
	 * Create a new error exception.
	 *
	 * @param code    for the error message.
	 * @param message a brief description of the error to be read by a human.
	 */
	public ErrorException(String code, String message) {

		super(message);
		this.code = code;

	}

	/**
	 * Create a new validation error exception with a message an a cause.
	 *
	 * @param code    for the error message.
	 * @param message a brief description of the error to be read by a human.
	 * @param cause   because the model is not right.
	 */
	public ErrorException(String code, String message, Throwable cause) {

		super(message, cause);
		this.code = code;

	}

	/**
	 * Create a new validation error exception with a message an a cause.
	 *
	 * @param code  for the error message.
	 * @param cause because the model is not right.
	 */
	public ErrorException(String code, Throwable cause) {

		super(cause);
		this.code = code;

	}

	/**
	 * Return the exception equivalent to an {@link ErrorMessage}.
	 *
	 * @param errorMessage to convert to an exception.
	 */
	public ErrorException(ErrorMessage errorMessage) {

		super(errorMessage.message);
		this.code = errorMessage.code;

	}

	/**
	 * The code associated to the error.
	 *
	 * @return the error code.
	 *
	 * @see #code
	 */
	public String getCode() {

		return this.code;
	}

}
