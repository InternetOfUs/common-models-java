Feature: Mock server for the WeNet task manager

Background:
	* configure cors = true
	* def Model = Java.type('eu.internetofus.common.components.Model')
	* def Task = Java.type('eu.internetofus.common.components.task_manager.Task')
	* def toTask = function(data){return Model.fromString(data, Task.class) }
	* def uuid = function(){ return java.util.UUID.randomUUID() + '' }
	* def tasks = {}
	* def TaskType = Java.type('eu.internetofus.common.components.task_manager.TaskType')
	* def toTaskType = function(data){return Model.fromString(data, TaskType.class) }
	* def taskTypes = {}

Scenario: pathMatches('/tasks/{taskId}') && methodIs('get') && tasks[pathParams.taskId] != null
    * def response = tasks[pathParams.taskId]

Scenario: pathMatches('/tasks/{taskId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Task associated to the ID."}

Scenario: pathMatches('/tasks') && methodIs('post') && toTask(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_task","message":"Bad task to store."}

Scenario: pathMatches('/tasks') && methodIs('post') && request.id == null
	* def task = request
	* task.id = uuid()
	* tasks[task.id] = task;
    * def response = task
    * def responseStatus = 201

Scenario: pathMatches('/tasks') && methodIs('post') && tasks[request.id] != null
    * def responseStatus = 400
    * def response = {"code":"bad_task","message":"Task already registered."}

Scenario: pathMatches('/tasks') && methodIs('post')
	* def task = request
	* tasks[task.id] = task;
    * def response = task
    * def responseStatus = 201

Scenario: pathMatches('/tasks/{taskId}') && methodIs('delete') && tasks[pathParams.taskId] != null
    * karate.remove('tasks', '$.' + pathParams.taskId)
    * def responseStatus = 204

Scenario: pathMatches('/tasks/{taskId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Task associated to the ID."}

Scenario: pathMatches('/tasks/{taskId}') && methodIs('patch') && tasks[pathParams.taskId] != null && toTask(karate.pretty(request)) != null
	* def task = request
	* task.id = pathParams.taskId
	* tasks[task.id] = task;
    * def response = task
    * def responseStatus = 200

Scenario: pathMatches('/tasks/{taskId}') && methodIs('patch') 
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Task associated to the ID."}

Scenario: pathMatches('/tasks/{taskId}') && methodIs('delete') && tasks[pathParams.taskId] != null
    * karate.remove('tasks', '$.' + pathParams.taskId)
    * def responseStatus = 204

Scenario: pathMatches('/tasks/{taskId}') && methodIs('patch')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Task associated to the ID."}

Scenario: pathMatches('/tasks/types/{taskTypeId}') && methodIs('get') && taskTypes[pathParams.taskTypeId] != null
    * def response = taskTypes[pathParams.taskTypeId]

Scenario: pathMatches('/tasks/types/{taskTypeId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No TaskType associated to the ID."}

Scenario: pathMatches('/tasks/types') && methodIs('post') && toTaskType(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_taskType","message":"Bad taskType to store."}

Scenario: pathMatches('/tasks/types') && methodIs('post') && request.id == null
	* def taskType = request
	* taskType.id = uuid()
	* taskTypes[taskType.id] = taskType;
    * def response = taskType
    * def responseStatus = 201

Scenario: pathMatches('/tasks/types') && methodIs('post') && taskTypes[request.id] != null
    * def responseStatus = 400
    * def response = {"code":"bad_taskType","message":"TaskType already registered."}

Scenario: pathMatches('/tasks/types') && methodIs('post')
	* def taskType = request
	* taskTypes[taskType.id] = taskType;
    * def response = taskType
    * def responseStatus = 201

Scenario: pathMatches('/tasks/types/{taskTypeId}') && methodIs('delete') && taskTypes[pathParams.taskTypeId] != null
    * karate.remove('taskTypes', '$.' + pathParams.taskTypeId)
    * def responseStatus = 204

Scenario: pathMatches('/tasks/types/{taskTypeId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No TaskType associated to the ID."}

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
