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

package eu.internetofus.common.components.task_manager;

import eu.internetofus.common.components.AbstractComponentMocker;
import eu.internetofus.common.components.CRUDContext;
import io.vertx.ext.web.Router;

/**
 * The mocked server for the {@link WeNetTaskManager}.
 *
 * @see WeNetTaskManager
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class WeNetTaskManagerMocker extends AbstractComponentMocker {

  /**
   * The context to do CRUD operations over the tasks.
   */
  protected CRUDContext tasksContext = new CRUDContext("id", "tasks", Task.class);

  /**
   * The context to do CRUD operations over the task types.
   */
  protected CRUDContext taskTypesContext = new CRUDContext("id", "taskTypes", TaskType.class);

  /**
   * Start a mocker builder into a random port.
   *
   * @return the started mocker.
   */
  public static WeNetTaskManagerMocker start() {

    return start(0);

  }

  /**
   * Start a mocker builder into a port.
   *
   * @param port to bind the server.
   *
   * @return the started mocker.
   */
  public static WeNetTaskManagerMocker start(final int port) {

    final var mocker = new WeNetTaskManagerMocker();
    mocker.startServerAndWait(port);
    return mocker;
  }

  /**
   * {@inheritDoc}
   *
   * @see WeNetTaskManagerClient#TASK_MANAGER_CONF_KEY
   */
  @Override
  protected String getComponentConfigurationName() {

    return WeNetTaskManagerClient.TASK_MANAGER_CONF_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void fillInRouterHandler(final Router router) {

    router.post("/tasks").handler(this.tasksContext.postHandler());
    router.get("/tasks/:id").handler(this.tasksContext.getHandler());
    router.get("/tasks").handler(this.tasksContext.getPageHandler());
    router.put("/tasks/:id").handler(this.tasksContext.putHandler());
    router.patch("/tasks/:id").handler(this.tasksContext.patchHandler());
    router.delete("/tasks/:id").handler(this.tasksContext.deleteHandler());

    router.post("/taskTypes").handler(this.taskTypesContext.postHandler());
    router.get("/taskTypes/:id").handler(this.taskTypesContext.getHandler());
    router.get("/taskTypes").handler(this.taskTypesContext.getPageHandler());
    router.put("/taskTypes/:id").handler(this.taskTypesContext.putHandler());
    router.patch("/taskTypes/:id").handler(this.taskTypesContext.patchHandler());
    router.delete("/taskTypes/:id").handler(this.taskTypesContext.deleteHandler());

    router.post("/tasks").handler(this.tasksContext.postHandler());
    router.get("/tasks/:id").handler(this.tasksContext.getHandler());
    router.get("/tasks").handler(this.tasksContext.getPageHandler());
    router.put("/tasks/:id").handler(this.tasksContext.putHandler());
    router.patch("/tasks/:id").handler(this.tasksContext.patchHandler());
    router.delete("/tasks/:id").handler(this.tasksContext.deleteHandler());

  }

}
