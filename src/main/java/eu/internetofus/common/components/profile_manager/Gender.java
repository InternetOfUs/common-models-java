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
package eu.internetofus.common.components.profile_manager;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describe the possible genders of a person.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@Schema(description = "Define the possible genders for an user.")
public enum Gender {

	/**
	 * A person whose gender identity matches to female.
	 */
	@Schema(description = "A person whose gender identity matches to female.")
	F,

	/**
	 * A person whose gender identity matches to male.
	 */
	@Schema(description = "A person whose gender identity matches to male.")
	M;
}
