/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -----------------------------------------------------------------------------
 */
package eu.internetofus.common.protocols;

import io.vertx.core.json.JsonArray;
import java.util.Random;

/**
 * Test the calculus when uses ‘basic needs’, ‘appreciating culture’,
 * ‘performing/producing culture’, ‘physical activities/sports’, or ‘things to
 * do about town’ domain.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PilotM46LSEProtocolWithOtherDomainsITC extends AbstractPilotM46LSEProtocolITC {

  /**
   * The possible domains for the test.
   */
  public static final String[] POSSIBLE_DOMAINS = { "basic_needs", "appreciating_culture",
      "performing_producing_culture", "physical_activities_sports", "things_to_do_about_town" };

  /**
   * {@inheritDoc} ‘random thoughts’ or ‘sensitive issues’
   */
  @Override
  protected String domain() {

    return POSSIBLE_DOMAINS[new Random().nextInt(POSSIBLE_DOMAINS.length)];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String domainInterest() {

    return "similar";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String beliefsAndValues() {

    return "similar";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String socialCloseness() {

    return "similar";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validMatchUsers(final JsonArray matchUsers) {

    return true;
  }

}
