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

/**
 * Component that provide the common component to check the M46 pilots
 * protocols.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public abstract class AbstractPilotM46WithCommonDomainProtocolITC extends AbstractPilotM46ProtocolITC {

  /**
   * The domains of the Pilot.
   */
  public enum Domain {

    /**
     * The academic skills domain.
     */
    ACADEMIC_SKILLS,
    /**
     * The basic needs domain.
     */
    BASIC_NEEDS,
    /**
     * The physical activities/sport domain.
     */
    PHYSICAL_ACTIVITY,
    /**
     * The appreciating culture domain.
     */
    APPRECIATING_CULTURE,
    /**
     * The random thoughts domain.
     */
    RANDOM_THOUGHTS,
    /**
     * The performing/producing culture domain.
     */
    PRODUCING_CULTURE,
    /**
     * The things to do about town domain.
     */
    LEISURE_ACTIVITIES,
    /**
     * The campus life domain.
     */
    CAMPUS_LIFE,
    /**
     * The sensitive issue domain.
     */
    SENSITIVE;

    /**
     * Return the value that can be used on the task type.
     *
     * @return the domain used on the task type for the domain.
     */
    public String toTaskTypeDomain() {

      return this.name().toLowerCase();

    }

  }

}
