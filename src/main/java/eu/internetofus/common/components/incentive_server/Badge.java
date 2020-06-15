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
 * The badge on an {@link Incentive}
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "Badge", description = "The badge on an incentive.")
public class Badge extends Model {

  /**
   * The badge class.
   */
  @Schema(example = "hpqAdI7hQf2maQ13AW1jXA")
  public String BadgeClass;

  /**
   * The badge image.
   */
  @Schema(example = "http://3.126.161.118:8000/media/uploads/badges/assertion-OYmfmtDFSIKG-qeZfXz4QQ.png")
  public String ImgUrl;

  /**
   * The badge criteria.
   */
  @Schema(example = "The user will get this badge for 50 relations in tweeter.")
  public String Criteria;

  /**
   * The badge message.
   */
  @Schema(example = "congratulations! you just earned a new badge for your relations on tweeter.")
  public String Message;

}
