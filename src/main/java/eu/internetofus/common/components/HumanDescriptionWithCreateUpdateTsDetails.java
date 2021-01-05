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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A model that has information when it is created and updated.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, description = "The attributes used to identify and describe a model for an human with timestamp details.")
public class HumanDescriptionWithCreateUpdateTsDetails extends CreateUpdateTsDetails {

  /**
   * A name that identify the type.
   */
  @Schema(description = "A name that identify the model.", example = "Eat together task")
  public String name;

  /**
   * A human readable description of the task type.
   */
  @Schema(description = "A human readable description of the model.", example = "A task for organizing social dinners")
  public String description;

  /**
   * A name that identify the type.
   */
  @ArraySchema(schema = @Schema(implementation = String.class), arraySchema = @Schema(description = "The keywords that describe the model.", example = "[\"social interaction\",\"eat\"]"))
  public List<String> keywords;

}
