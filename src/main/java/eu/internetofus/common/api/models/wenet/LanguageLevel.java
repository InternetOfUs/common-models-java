/*
 * -----------------------------------------------------------------------------
 *
 * Copyright 2019 - 2022 by
 *
 * - Spanish Scientific Research Council (CSIC) Institut d'Investigació en
 * Intel·ligència Artificial (IIIA) Unitat de Desenvolupament Tecnológic
 * (UDT-IA)
 *
 * See the 'LICENSE.md' file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * -----------------------------------------------------------------------------
 */
package eu.internetofus.common.api.models.wenet;

/**
 * The levels that define the linguistic ability of a person in a language
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum LanguageLevel {

	/**
	 * Proficient. The person is fluent, pretty much mother tongue. Extremely
	 * comfortable, it has complete control over the language.
	 */
	C2,
	/**
	 * Advanced. Comfortable in most situations, strong vocabulary, few errors.
	 */
	C1,
	/**
	 * Upper-intermediate. The person is comfortable in most situations, still some
	 * good mistakes.
	 */
	B2,
	/**
	 * Intermediate. Now the person can converse in many situations, with less
	 * serious errors.
	 */
	B1,
	/**
	 * Pre-intermediate. Limited vocabulary, but with some help the person can
	 * participate in basic conversations. It stills make a lot of big mistakes.
	 */
	A2,
	/**
	 * Elementary. The person can ask a few basic questions and ideas, but with a
	 * lot of mistakes.
	 */
	A1,
	/**
	 * Beginner/False beginner. The person only knows a few words of the language,
	 * if that.
	 */
	A0;
}
