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
 * The default protocols that can be used in the WeNet platform.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum DefaultProtocols implements DefaultProtocol {

  /**
   * The second version of the protocol to create a social event.
   */
  EAT_TOGETHER_V2("eat_together_v2"),

  /**
   * The first version of the protocol to ask for help.
   */
  ASK_4_HELP_V1("ask4help_v1"),

  /**
   * The revision of the protocol to ask for help. This use the new automatic
   * predicates that notify the incentive server.
   */
  ASK_4_HELP_V1_2("ask4help_v1_2"),

  /**
   * The second version of the protocol to ask for help. This add new attributes
   * to select who to ask.
   */
  ASK_4_HELP_V2("ask4help_v2"),

  /**
   * The new version of the second version of the protocol to ask for help where
   * do to have social closeness or position to answer.
   */
  ASK_4_HELP_V2_2("ask4help_v2_2"),

  /**
   * The second version of the protocol to ask for help. This use the new
   * automatic predicates that notify the incentive server.
   */
  ECHO_V1("echo_v1");

  /**
   * The default identifier associated to the protocol.
   */
  protected String id;

  /**
   * Create a new protocol.
   *
   * @param id identifier of the protocol.
   */
  private DefaultProtocols(final String id) {

    this.id = id;
  }

  /**
   * {@inheritDoc}
   *
   * @see #id
   */
  @Override
  public String taskTypeId() {

    return this.id;
  }
}
