Feature: Mock server for the WeNet service

Background:
	* configure cors = true
	* def Model = Java.type('eu.internetofus.common.components.Model')
	* def App = Java.type('eu.internetofus.common.components.service.App')
	* def toApp = function(data){return Model.fromString(data, App.class) }
	* def uuid = function(){ return java.util.UUID.randomUUID() + '' }
	* def createMessageCallbackUrlFor = function(id){ return 'http://localhost:'+karate.properties['wenet.component.service.port']+'/callback/'+id}
	* def apps = {"1":{"appId":"1","messageCallbackUrl":"#(createMessageCallbackUrlFor(1))"}}
	* def users = {"1":["1","2","3","4","5"]}
	* def callbacks = {}

Scenario: pathMatches('/app/{appId}') && methodIs('get') && apps[pathParams.appId] != null
    * def response = apps[pathParams.appId]

Scenario: pathMatches('/app/{appId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app') && methodIs('post') && toApp(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"Bad app to store."}

Scenario: pathMatches('/app') && methodIs('post') && request.appId == null
	* def app = request
	* app.appId = uuid()
	* app.messageCallbackUrl = createMessageCallbackUrlFor(app.appId)
	* apps[app.appId] = app;
    * def response = app
    * def responseStatus = 201

Scenario: pathMatches('/app') && methodIs('post') && apps[request.appId] != null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"App already registered."}

Scenario: pathMatches('/app') && methodIs('post')
	* def app = request
	* app.messageCallbackUrl = createMessageCallbackUrlFor(app.appId)
	* apps[app.appId] = app;
    * def response = app
    * def responseStatus = 201

Scenario: pathMatches('/app/{appId}') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('apps', '$.' + pathParams.appId)
    * karate.remove('users', '$.' + pathParams.appId)
    * karate.remove('callbacks', '$.' + pathParams.appId)
    * def responseStatus = 204

Scenario: pathMatches('/app/{appId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('get') && users[pathParams.appId] != null
    * def response = users[pathParams.appId]

Scenario: pathMatches('/app/{appId}/users') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post')
	* def newUsers = request
	* users[pathParams.appId] = newUsers;
    * def response = newUsers
    * def responseStatus = 201

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('users', '$.' + pathParams.appId)
    * def responseStatus = 204

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('get') && callbacks[pathParams.appId] != null
    * def response = callbacks[pathParams.appId]

Scenario: pathMatches('/callback/{appId}s') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('post')
	* def newCallbacks = request
	* callbacks[pathParams.appId] = newCallbacks;
    * def response = newCallbacks
    * def responseStatus = 201

Scenario: pathMatches('/callback/{appId}') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('callbacks', '$.' + pathParams.appId)
    * def responseStatus = 204

Scenario: pathMatches('/callback/{appId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
