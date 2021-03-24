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
