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

package eu.internetofus.common.components.profile_manager;

/**
 * Type of aggregation function used to calculate the trust.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum TrustAggregator {

  /**
   * This is based on the recency rating events. It is the function used on uHelp.
   */
  RECENCY_BASED,

  /**
   * The trust is the average of the rating event.
   */
  AVERAGE,

  /**
   * The trust is the median of the rating event.
   */
  MEDIAN,

  /**
   * The trust is the minimum rating.
   */
  MINIMUM,

  /**
   * The trust is the maximum rating.
   */
  MAXIMUM;

}
