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
package eu.internetofus.common.vertx.basic;

import eu.internetofus.common.model.Model;
import eu.internetofus.common.model.ReflectionModel;
import java.util.List;

/**
 * Contains the found users.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class UsersPage extends ReflectionModel implements Model {

  /**
   * The index of the first user returned.
   */
  public int offset;

  /**
   * The number total of user that satisfies the search.
   */
  public int total;

  /**
   * The found profiles.
   */
  public List<User> users;

}
