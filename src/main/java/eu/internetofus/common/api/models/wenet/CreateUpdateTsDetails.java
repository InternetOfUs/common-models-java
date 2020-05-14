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

import eu.internetofus.common.TimeManager;
import eu.internetofus.common.api.models.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A model that has information when it is created and updated.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "Generic model for the models that can be created and updated.")
public class CreateUpdateTsDetails extends Model {

	/**
	 * The instant of the creation.
	 */
	@Schema(description = "The time stamp representing the account creation instant.", example = "1563871899")
	public long _creationTs;

	/**
	 * The instant of the last update.
	 */
	@Schema(description = "The time stamp representing the last update instant.", example = "1563898764")
	public long _lastUpdateTs;

	/**
	 * Create a new model.
	 */
	public CreateUpdateTsDetails() {

		this._creationTs = this._lastUpdateTs = TimeManager.now();

	}
}
