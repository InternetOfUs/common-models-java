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

import eu.internetofus.common.model.TimeManager;
import io.vertx.core.json.JsonObject;
import java.time.Duration;

/**
 * Test the pilot M46 at LSE.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46LSEProtocolITC extends AbstractPilotM46ProtocolITC {

  /**
   * The domains of the LSE.
   */
  public enum Domain {

    /**
     * The basic needs domain.
     */
    BASIC_NEEDS,
    /**
     * The campus life domain.
     */
    CAMPUS_LIFE,
    /**
     * The academic skills domain.
     */
    ACADEMIC_SKILLS,
    /**
     * The appreciating culture domain.
     */
    APPRECIATING_CULTURE,
    /**
     * The performing/producing culture domain.
     */
    PERFORMING_PRODUCING_CULTURE,
    /**
     * The physical activities/sport domain.
     */
    PHYSICAL_ACTIVITIES_SPORTS,
    /**
     * The things to do about town domain.
     */
    THINGS_TO_DO_ABOUT_TOWN,
    /**
     * The random thoughts domain.
     */
    RANDOM_THOUGHTS,
    /**
     * The sensitive issue domain.
     */
    SENSITIVE_ISSUES;

    /**
     * Return the value that can be used on the task type.
     *
     * @return the domain used on the task type for the domain.
     */
    public String toTaskTypeDomain() {

      return this.name().toLowerCase();

    }

  }

  /**
   * {@inheritDoc}
   *
   * @see DefaultProtocols#PILOT_M46_LSE
   */
  @Override
  protected DefaultProtocols getDefaultProtocolsToUse() {

    return DefaultProtocols.PILOT_M46_LSE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int maxUsers() {

    return 5;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int maxAnswers() {

    return 7;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean sensitive() {

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean anonymous() {

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long expirationDate() {

    return TimeManager.now() + Duration.ofDays(1).toSeconds();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String positionOfAnswerer() {

    return "anywhere";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean validClosenessUsers(final JsonObject state) {

    return !state.containsKey("closenessUsers");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Double indifferentValue() {

    return null;
  }
}
