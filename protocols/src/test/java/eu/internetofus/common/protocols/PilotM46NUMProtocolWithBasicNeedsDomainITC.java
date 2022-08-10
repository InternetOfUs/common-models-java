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
 * Test the calculus when uses ‘basic needs’ domain.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PilotM46NUMProtocolWithBasicNeedsDomainITC extends AbstractPilotM46NUMProtocolWithDimensionITC {

  /**
   * {@inheritDoc} ‘random thoughts’ or ‘sensitive issues’
   */
  @Override
  protected String domain() {

    return Domain.BASIC_NEEDS.toTaskTypeDomain();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String domainInterest() {

    return "different";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String beliefsAndValues() {

    return "different";
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
  protected String positionOfAnswerer() {

    return "nearby";
  }

}
