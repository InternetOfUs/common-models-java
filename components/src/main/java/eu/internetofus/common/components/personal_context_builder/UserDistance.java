/*
 * -----------------------------------------------------------------------------
 *
 *   Copyright 2019 - 2022 UDT-IA, IIIA-CSIC
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

package eu.internetofus.common.components.personal_context_builder;

import eu.internetofus.common.components.Model;
import eu.internetofus.common.components.ReflectionModel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The distance of an user into a location.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "UserDistance", description = "The distance of an user from a location.")
public class UserDistance extends ReflectionModel implements Model {

  /**
   * The identifier of the user.
   */
  @Schema(description = "The Id of the user that this is its distance to a location.", example = "3e557acc-e846-4736-8218-3f64d8e68d8c")
  public String userId;

  /**
   * The latitude of the location.
   */
  @Schema(description = "The distance of the user from the location", example = "0")
  public double distance;

  /**
   * Calculate the distance between two positions. This is calculate with the
   * (<a href="https://www.movable-type.co.uk/scripts/latlong.html">haversine
   * formula</a>.
   *
   * @param sourceLatitude  source location latitude.
   * @param sourceLongitude source location longitude.
   * @param targetLatitude  source location latitude.
   * @param targetLongitude source location longitude.
   *
   * @return the distance in meters between the two locations.
   */
  public static double calculateDistance(final double sourceLatitude, final double sourceLongitude,
      final double targetLatitude, final double targetLongitude) {

    final var R = 6371e3; // metres
    final var φ1 = sourceLatitude * Math.PI / 180; // φ, λ in radians
    final var φ2 = targetLatitude * Math.PI / 180;
    final var Δφ = (targetLatitude - sourceLatitude) * Math.PI / 180;
    final var Δλ = (targetLongitude - sourceLongitude) * Math.PI / 180;

    final var a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2)
        + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    final var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // in metres

  }
}
