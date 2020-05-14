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

package eu.internetofus.common.api.models.wenet;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.internetofus.common.api.models.JsonObjectDeserializer;
import eu.internetofus.common.api.models.Model;
import eu.internetofus.common.api.models.Validable;
import eu.internetofus.common.api.models.ValidationErrorException;
import eu.internetofus.common.api.models.Validations;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * A message that can be interchange in an interaction protocol.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(name = "message", description = "A message that can be interchange in an interaction protocol.")
public class InteractionProtocolMessage extends Model implements Validable {

	/**
	 * The identifier of the user that is sending the message.
	 */
	@Schema(
			description = "The identifier of the user that send the message",
			example = "15837028-645a-4a55-9aaf-ceb846439eba")
	public String senderId;

	/**
	 * The identifier of the application that the user has used to send the message.
	 */
	@Schema(
			description = "The identifier of the application that has used the sender to send this message.",
			example = "E34jhg78tbgh")
	public String appId;

	/**
	 * The identifier of the community where the message will be said.
	 */
	@Schema(
			description = "The identifier of the application that has used the sender to send this message.",
			example = "ceb846439eba-645a-9aaf-4a55-15837028")
	public String communityId;

	/**
	 * The identifier of the task that the message is related.
	 */
	@Schema(
			description = "The identifier of the application that has used the sender to send this message.",
			example = "b129e5509c9bb79")
	public String taskId;

	/**
	 * The content of the message.
	 */
	@Schema(description = "The content of the message.", example = "Hi!", type = "object")
	@JsonDeserialize(using = JsonObjectDeserializer.class)
	public Object content;

	/**
	 * The norms that has to be applied over the message.
	 */
	@ArraySchema(
			schema = @Schema(
					ref = "https://bitbucket.org/wenet/wenet-components-documentation/raw/master/sources/wenet-models-openapi.yaml#/components/schemas/Norm"),
			arraySchema = @Schema(description = "The norms to apply over the message"))
	public List<Norm> norms;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<Void> validate(String codePrefix, Vertx vertx) {

		final Promise<Void> promise = Promise.promise();
		Future<Void> future = promise.future();
		try {

			this.appId = Validations.validateNullableStringField(codePrefix, "appId", 255, this.appId);
			this.communityId = Validations.validateNullableStringField(codePrefix, "communityId", 255, this.communityId);
			this.taskId = Validations.validateNullableStringField(codePrefix, "taskId", 255, this.taskId);
			this.senderId = Validations.validateNullableStringField(codePrefix, "senderId", 255, this.senderId);
			if (this.content == null) {

				promise.fail(new ValidationErrorException(codePrefix + ".content", "You must to define a content"));

			} else {

				future = future.compose(Validations.validate(this.norms, (a, b) -> a.equals(b), codePrefix + ".norms", vertx));
				promise.complete();

			}

		} catch (final ValidationErrorException validationError) {

			promise.fail(validationError);
		}

		return future;

	}

}
