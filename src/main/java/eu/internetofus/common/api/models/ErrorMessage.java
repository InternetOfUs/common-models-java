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
package eu.internetofus.common.api.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic model to return the that describe an error reported
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "ErrorMessage", description = "Inform of an error that happens when interacts with the API")
public class ErrorMessage extends Model {

	/**
	 * The code of the error.
	 */
	@Schema(description = "Contain code that identifies the error", example = "error_code")
	public String code;

	/**
	 * A brief description of the error to be read by a human.
	 */
	@Schema(
			description = "Contain a brief description of the error to be read by a human",
			example = "Error readable by a human")
	public String message;

	/**
	 * Create empty error message.
	 */
	public ErrorMessage() {

	}

	/**
	 * Create a new error message.
	 *
	 * @param code    for the error message.
	 * @param message a brief description of the error to be read by a human.
	 */
	public ErrorMessage(String code, String message) {

		this.code = code;
		this.message = message;

	}
}