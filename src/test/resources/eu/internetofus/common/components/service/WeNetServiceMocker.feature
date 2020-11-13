Feature: Mock server for the WeNet service

Background:
	* configure cors = true
	* def Model = Java.type('eu.internetofus.common.components.Model')
	* def App = Java.type('eu.internetofus.common.components.service.App')
	* def toApp = function(data){return Model.fromString(data, App.class) }
	* def uuid = function(){ return java.util.UUID.randomUUID() + '' }
	* def createMessageCallbackUrlFor = function(requestUrlBase,id){return requestUrlBase+'/callback/'+id}
	* def apps = {}
	* def users = {}
	* def callbacks = {}

Scenario: pathMatches('/app/{appId}') && methodIs('get') && apps[pathParams.appId] != null
    * def response = apps[pathParams.appId]
    * karate.log('For the id ',pathParams.appId,' the app is ',response)

Scenario: pathMatches('/app/{appId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app') && methodIs('post') && toApp(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"Bad app to store."}

Scenario: pathMatches('/app') && methodIs('post') && request.appId == null
	* def app = request
	* app.appId = uuid()
	* app.messageCallbackUrl = createMessageCallbackUrlFor(requestUrlBase,app.appId)
	* apps[app.appId] = app;
    * def response = app
    * def responseStatus = 201
    * karate.log('Created app:',app)

Scenario: pathMatches('/app') && methodIs('post') && apps[request.appId] != null
    * def responseStatus = 400
    * def response = {"code":"bad_app","message":"App already registered."}

Scenario: pathMatches('/app') && methodIs('post')
	* def app = request
	* app.messageCallbackUrl = createMessageCallbackUrlFor(requestUrlBase,app.appId)
	* apps[app.appId] = app;
    * def response = app
    * def responseStatus = 201
    * karate.log('Created app:',app)

Scenario: pathMatches('/app/{appId}') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('apps', '$.' + pathParams.appId)
    * karate.remove('users', '$.' + pathParams.appId)
    * karate.remove('callbacks', '$.' + pathParams.appId)
    * def responseStatus = 204
    * karate.log('Deleted app:',pathParams.appId)

Scenario: pathMatches('/app/{appId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('get') && users[pathParams.appId] != null
    * def response = users[pathParams.appId]

Scenario: pathMatches('/app/{appId}/users') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 404
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/app/{appId}/users') && methodIs('post')
	* def newUsers = request
	* users[pathParams.appId] = newUsers;
    * def response = newUsers
    * def responseStatus = 201
    * karate.log('Stored on the app ',pathParams.appId,' the users ',newUsers)

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('users', '$.' + pathParams.appId)
    * def responseStatus = 204
    * karate.log('Delete user on the app ',pathParams.appId)

Scenario: pathMatches('/app/{appId}/users') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('get') && callbacks[pathParams.appId] != null
    * def response = callbacks[pathParams.appId]
    * karate.log('On the app ',pathParams.appId,' the callbacks are ',response)

Scenario: pathMatches('/callback/{appId}') && methodIs('get') && apps[pathParams.appId] != null
    * def response = []
    * karate.log('On the app ',pathParams.appId,' the callbacks are []')

Scenario: pathMatches('/callback/{appId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('post') && apps[pathParams.appId] == null
    * def responseStatus = 404
    * def response = {"code":"bad_app","message":"No app associated to the ID."}

Scenario: pathMatches('/callback/{appId}') && methodIs('post')
	* def currentCallbacks = (callbacks[pathParams.appId] || [])  
	* def void = ( currentCallbacks.add(request) )
	* callbacks[pathParams.appId] = currentCallbacks;
    * def response = request
    * def responseStatus = 201
    * karate.log('Stored on the app ',pathParams.appId,' the callbacks ',currentCallbacks)

Scenario: pathMatches('/callback/{appId}') && methodIs('delete') && apps[pathParams.appId] != null
    * karate.remove('callbacks', '$.' + pathParams.appId)
    * def responseStatus = 204
    * karate.log('Delete callback on the app ',pathParams.appId)

Scenario: pathMatches('/callback/{appId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No App associated to the ID."}
    
Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
