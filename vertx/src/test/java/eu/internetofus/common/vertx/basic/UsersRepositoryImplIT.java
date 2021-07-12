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

import static org.assertj.core.api.Assertions.assertThat;

import eu.internetofus.common.vertx.Repository;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test the integration of the {@link UsersRepositoryImpl}.
 *
 * @see UsersRepositoryImpl
 *
 * @author UDT-IA, IIIA-CSIC
 */
@ExtendWith(BasicIntegrationExtension.class)
public class UsersRepositoryImplIT {

  /**
   * The number maximum of model to convert.
   */
  private static final int MAX = 10000;

  /**
   * Should migrate to the current version. This unit test is added because on the
   * migration process provokes a {@link StackOverflowError} ehne the database has
   * more than 6000/7000 entries.
   *
   * @param vertx       event bus to use.
   * @param testContext context that executes the test.
   *
   * @see UsersRepositoryImpl#migrateDocumentsToCurrentVersions()
   */
  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.MINUTES)
  @DisabledIfSystemProperty(named = "disable.large.unit.tests", matches = "true", disabledReason = "The integration test require around 10 minutes disabled.")
  public void shoudMigrateDocumentsToCurrentVersions(final Vertx vertx, final VertxTestContext testContext) {

    final var config = BasicIntegrationExtension.container.getMongoDBConfig();
    final var client = MongoClient.create(vertx, config);

    final Promise<Void> stored = Promise.promise();
    final List<User> expectedUsers = new ArrayList<>();
    client.dropCollection(UsersRepositoryImpl.USERS_COLLECTION).onComplete(testContext.succeeding(dropped -> {

      for (var i = 0; i < MAX; i++) {

        final var user = new UserTest().createModelExample(i);
        user._id = null;
        final var oldUser = user.toJsonObject().put(Repository.SCHEMA_VERSION, "0").put("extraValue", true)
            .put("extraField", new JsonObject().put("key", "1").put("index", i).put("isTrue", true)).put("norms",
                new JsonArray()
                    .add(new JsonObject().put("conditions", new JsonArray().add("condition1").add("condition2")))
                    .add(new JsonObject().put("actions", new JsonArray().add("action1").add("action2"))));
        client.insert(UsersRepositoryImpl.USERS_COLLECTION, oldUser).onComplete(testContext.succeeding(id -> {

          user._id = id;
          expectedUsers.add(user);
          if (expectedUsers.size() == MAX) {

            stored.complete();
          }
        }));
      }

    }));

    final var repository = new UsersRepositoryImpl(vertx, client, "1");
    stored.future().compose(empty -> {
      return repository.migrateDocumentsToCurrentVersions();
    }).compose(empty -> repository.retrieveUsersPage(0, MAX))
        .onComplete(testContext.succeeding(page -> testContext.verify(() -> {

          assertThat(page.total).isEqualTo(MAX);
          for (final var user : page.users) {

            var found = false;
            final var i = expectedUsers.iterator();
            while (i.hasNext()) {

              final var expectedUser = i.next();
              if (expectedUser._id.equals(user._id)) {

                assertThat(user).isEqualTo(expectedUser);
                i.remove();
                found = true;
              }

            }
            assertThat(found).withFailMessage("Not found %s", user).isTrue();

          }
          assertThat(expectedUsers).isEmpty();
          testContext.completeNow();

        })));

  }

}
