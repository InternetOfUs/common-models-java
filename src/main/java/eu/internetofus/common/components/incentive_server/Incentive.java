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

package eu.internetofus.common.components.incentive_server;

import eu.internetofus.common.components.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents an incentive for an user.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Incentive", description = "An user incentive.")
public class Incentive extends Model {

  /**
   * Identifier of the application.
   */
  @Schema(example = "1")
  public String AppID;


  /**
   * Identifier of the user.
   */
  @Schema(example = "6")
  public String UserId;

  /**
   * type of incentive.
   */
  @Schema(example = "Message/Badge (only one)")
  public String IncentiveType;

  /**
   * Identifier of the application.
   */
  @Schema(example = "WeNet issuer")
  public String Issuer;

  /**
   * Identifier of the application.
   */
  @Schema(description="Message of the icentive")
  public Message Message;

  /**
   * Identifier of the application.
   */
  @Schema(description="Badge of the icentive")
  public Badge Badge;

}
