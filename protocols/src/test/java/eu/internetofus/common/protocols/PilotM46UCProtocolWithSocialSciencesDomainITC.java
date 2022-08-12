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
 * Test the calculus when uses
 * {@link AbstractPilotM46UCProtocolITC.Domain#SOCIAL_SCIENCES}.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PilotM46UCProtocolWithSocialSciencesDomainITC extends AbstractPilotM46UCProtocolWithDimensionITC {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String domain() {

    return Domain.SOCIAL_SCIENCES.toTaskTypeDomain();
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
  protected String positionOfAnswerer() {

    return "nearby";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String competences() {

    return "relevant";
  }

}
