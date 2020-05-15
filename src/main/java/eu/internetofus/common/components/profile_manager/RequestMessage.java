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

package eu.internetofus.common.components.profile_manager;

import java.util.List;

import eu.internetofus.common.components.Model;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A message that can be send into an application.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(hidden = true, name = "message", description = "A message to interact with an application.")
public class RequestMessage extends Model {

	/**
	 * The identifier of the message.
	 */
	@Schema(description = "The identifier of the message.", example = "2A6F67A4-42D2-4DE0-9F00-DE4A564A83A8")
	public String messageId;

	/**
	 * The possible channel where the message can be send.
	 */
	public enum Channel {
		/**
		 * Send via facebook.
		 */
		facebook

	}

	/**
	 * The channel used to send the message.
	 */
	@Schema(description = "The channel used to send the message.", example = "facebook")
	public Channel channel;

	/**
	 * The identifier of the application message.
	 */
	@Schema(description = "The identifier of the application to send the message.", example = "USR-JDKHEIU2-31NJDTE94")
	public String appId;

	/**
	 * The possible message types.
	 */
	public enum Type {
		/**
		 * The message is a request.
		 */
		request

	}

	/**
	 * The type of the message.
	 */
	@Schema(description = "The type of message.", example = "request")
	public Type type;

	/**
	 * The timestamp of the message.
	 */
	@Schema(description = "The timestamp when the message is send.", example = "2019-04-04:23.11.58")
	public String timestamp;

	/**
	 * A content of the message.
	 */
	public static class Content {

		/**
		 * create a content.
		 */
		protected Content() {

		}

		/**
		 * Create a textual message.
		 *
		 * @param value of the textual message.
		 *
		 * @return the new textual message.
		 */
		public static TextualMessage createTextual(String value) {

			final TextualMessage textual = new TextualMessage();
			textual.value = value;
			return textual;
		}

		/**
		 * Create a action request.
		 *
		 * @param payload of the action request.
		 * @param value   of the action request.
		 *
		 * @return the new action request.
		 */
		public static ActionRequest createAction(String payload, String value) {

			final ActionRequest action = new ActionRequest();
			action.payload = payload;
			action.value = value;
			return action;
		}

	}

	/**
	 * The textual message.
	 */
	public static class TextualMessage extends Content {

		/**
		 * The type of content.
		 */
		@Schema(description = "The content of the message.")
		public String type = "textual_message";

		/**
		 * The type of content.
		 */
		@Schema(description = "The value of the textual message.", example = "I need someone to pick up my son at school")
		public String value;

	}

	/**
	 * The textual message.
	 */
	public static class ActionRequest extends Content {

		/**
		 * The type of content.
		 */
		@Schema(description = "The content of the message.")
		public String type = "textual_message";

		/**
		 * The type of content.
		 */
		@Schema(description = "The payload of the message.", example = "volunteer")
		public String payload = "textual_message";

		/**
		 * The type of content.
		 */
		@Schema(description = "The value of the action request.", example = "I can do it!")
		public String value;

	}

	/**
	 * The content of the message.
	 */
	@Schema(description = "The content of the message.")
	public Content content;

	/**
	 * The domain of the message.
	 */
	@Schema(description = "The domain of the message.", example = "help")
	public String domain;

	/**
	 * An intent of the message.
	 */
	public static class Intent {

		/**
		 * The name of the intent.
		 */
		@Schema(description = "The name of the intent.", example = "help")
		public String name;

		/**
		 * The confidence of the intent.
		 */
		@Schema(description = "The confidence of the intent.", example = "0.87")
		public Float confidnce;

	}

	/**
	 * The intent of the message.
	 */
	@Schema(description = "The intent of the message.")
	public Intent intent;

	/**
	 * An entity of the message.
	 */
	public static class Entity {

		/**
		 * The name of the entity.
		 */
		@Schema(description = "The name of the entity.", example = "location")
		public String name;

		/**
		 * The value of the entity.
		 */
		@Schema(description = "The value of the entity.", example = "school")
		public String value;

		/**
		 * The confidence of the entity.
		 */
		@Schema(description = "The confidence of the entity.", example = "0.94")
		public Float confidnce;

	}

	/**
	 * The entities of the message.
	 */
	@Schema(description = "The entities of the message.")
	public List<Entity> entities;

	/**
	 * The language of the message.
	 */
	@Schema(description = "The language of the message.", example = "it")
	public String language;

	/**
	 * The message metadata.
	 */
	@Schema(description = "The metadata of the message.")
	public Object metadata;

}
