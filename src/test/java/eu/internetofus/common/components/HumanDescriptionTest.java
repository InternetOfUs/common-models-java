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

import java.util.ArrayList;

/**
 * Test the {@link HumanDescription}
 *
 * @see HumanDescription
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class HumanDescriptionTest extends ModelTestCase<HumanDescription> {

  /**
   * {@inheritDoc}
   */
  @Override
  public HumanDescription createModelExample(int index) {

    var model = new HumanDescription();
    model.name = "name_" + index;
    model.description = "description_" + index;
    model.keywords = new ArrayList<>();
    model.keywords.add("keyword_" + (index - 1));
    model.keywords.add("keyword_" + index);
    model.keywords.add("keyword_" + (index + 1));
    return model;
  }

}
