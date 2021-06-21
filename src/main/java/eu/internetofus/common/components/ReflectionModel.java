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

package eu.internetofus.common.components;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A model that use reflections to do the basic operations.
 *
 * @author UDT-IA, IIIA-CSIC
 */
@JsonInclude(Include.NON_EMPTY)
public class ReflectionModel {

  /**
   * Reflections equals.
   *
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   * @see EqualsBuilder#reflectionEquals(Object, Object,String...)
   */
  @Override
  public boolean equals(final Object obj) {

    return EqualsBuilder.reflectionEquals(this, obj);

  }

  /**
   * Reflection hash code.
   *
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   * @see HashCodeBuilder#reflectionHashCode(Object, String...)
   */
  @Override
  public int hashCode() {

    return HashCodeBuilder.reflectionHashCode(this);

  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   * @see ToStringBuilder#reflectionToString(Object)
   */
  @Override
  public String toString() {

    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);

  }

}
