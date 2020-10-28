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

package eu.internetofus.common.components.profile_manager;

import java.util.ArrayList;

import eu.internetofus.common.components.ModelTestCase;

/**
 * Test the {@link CommunityProfilesPage}.
 *
 * @see CommunityProfilesPage
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class CommunityProfilesPageTest extends ModelTestCase<CommunityProfilesPage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public CommunityProfilesPage createModelExample(final int index) {

    final var model = new CommunityProfilesPage();
    model.offset = index;
    model.total = 3 + 10 * index;
    model.communities = new ArrayList<>();
    for (var i = 0; i < 3; i++) {

      final var profile = new CommunityProfileTest().createModelExample(index + i);
      model.communities.add(profile);

    }

    return model;
  }

}
