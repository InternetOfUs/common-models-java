Feature: Mock server for the WeNet service

Background:
	* configure cors = true
	* def Model = Java.type('eu.internetofus.common.components.Model')
	* def App = Java.type('eu.internetofus.common.components.service.App')
	* def toApp = function(data){return Model.fromString(data, App.class) }
	* def uuid = function(){ return java.util.UUID.randomUUID() + '' }
	* def createMessageCallbackUrlFor = function(requestUrlBase,id){return requestUrlBase+'/app/'+id+'/callbacks'}
	* def apps = {}

Scenario: pathMatches('/app/{appId}') && methodIs('get') && apps[pathParams.appId] != null
    * def response = apps[pathParams.appId].model
    * karate.log('For the id ',pathParams.appId,' the app is ',response)

Scenario: pathMatches('/app/{appId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app') && methodIs('post') && toApp(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"Bad app to store."}

Scenario: pathMatches('/app') && methodIs('post') && request.appId == null
	* def app = {}
	* app.model = request
	* app.model.appId = uuid()
	* app.model.messageCallbackUrl = createMessageCallbackUrlFor(requestUrlBase,app.model.appId)
	* app.users = []
	* app.callbacks = []
	* apps[app.model.appId] = app;
    * def response = app.model
    * def responseStatus = 201
    * karate.log('Created app:',app)

Scenario: pathMatches('/app') && methodIs('post') && apps[request.appId] != null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"App already registered."}

Scenario: pathMatches('/app') && methodIs('post')
	* def app = {}
	* app.model = request
	* app.users = []
	* app.callbacks = []
	* app.model.messageCallbackUrl = createMessageCallbackUrlFor(requestUrlBase,app.model.appId)
	* apps[app.model.appId] = app;
    * def response = app.model
    * def responseStatus = 201
    * karate.log('Created app:',app)

Scenario: pathMatches('/app/{appId}') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('apps', '$.' + pathParams.appId)
    * def responseStatus = 204
    * def response = []
    * karate.log('Deleted app:',pathParams.appId)

Scenario: pathMatches('/app/{appId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('get') && apps[pathParams.appId] != null
    * def response = apps[pathParams.appId].users

Scenario: pathMatches('/app/{appId}/users') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 404
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post')
	* def newUsers = request
	* apps[pathParams.appId].users = newUsers;
    * def responseStatus = 204
    * def response = []
    * karate.log('Stored on the app ',pathParams.appId,' the users ',newUsers)

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete') && apps[pathParams.appId] != null
    * apps[pathParams.appId].users = []
    * def responseStatus = 204
    * def response = []
    * karate.log('Delete user on the app ',pathParams.appId)

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('get') && apps[pathParams.appId] != null
    * def response = apps[pathParams.appId].callbacks
    * karate.log('On the app ',pathParams.appId,' the callbacks are ',response)

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 404
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('post')
	* def currentCallbacks = (apps[pathParams.appId].callbacks || [])  
	* def void = ( currentCallbacks.add(request) )
	* apps[pathParams.appId].callbacks = currentCallbacks;
    * def response = request
    * def responseStatus = 201
    * karate.log('Stored on the app ',pathParams.appId,' the callbacks ',currentCallbacks)

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('delete') && apps[pathParams.appId] != null
    * apps[pathParams.appId].callbacks = []
    * def responseStatus = 204
    * def response = []
    * karate.log('Delete callback on the app ',pathParams.appId)

Scenario: pathMatches('/app/{appId}/callbacks') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/apps') && methodIs('get')
    * def responseStatus = 200
    * def response = apps
    
Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
