Feature: Mock server for the WeNet profile manager

Background:
	* configure cors = true
	* def Model = Java.type('eu.internetofus.common.components.Model')
	* def WeNetUserProfile = Java.type('eu.internetofus.common.components.profile_manager.WeNetUserProfile')
	* def CommunityProfile = Java.type('eu.internetofus.common.components.profile_manager.CommunityProfile')
	* def toProfile = function(data){return Model.fromString(data, WeNetUserProfile.class) }
	* def toCommunity = function(data){return Model.fromString(data, CommunityProfile.class) }
	* def uuid = function(){ return java.util.UUID.randomUUID() + '' }
	* def profiles = {}
	* def communities = {}

Scenario: pathMatches('/profiles/{userId}') && methodIs('get') && profiles[pathParams.userId] != null
    * def response = profiles[pathParams.userId]

Scenario: pathMatches('/profiles/{userId}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Profile associated to the ID."}

Scenario: pathMatches('/profiles') && methodIs('post') && toProfile(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_profile","message":"Bad profile to store."}

Scenario: pathMatches('/profiles') && methodIs('post') && request.id == null
	* def profile = request
	* profile.id = uuid()
	* profiles[profile.id] = profile;
    * def response = profile
    * def responseStatus = 201

Scenario: pathMatches('/profiles') && methodIs('post') && profiles[request.id] != null
    * def responseStatus = 400
    * def response = {"code":"bad_profile","message":"Profile already registered."}

Scenario: pathMatches('/profiles') && methodIs('post')
	* def profile = request
	* profiles[profile.id] = profile;
    * def response = profile
    * def responseStatus = 201

Scenario: pathMatches('/profiles/{userId}') && methodIs('delete') && profiles[pathParams.userId] != null
    * karate.remove('profiles', '$.' + pathParams.userId)
    * def responseStatus = 204

Scenario: pathMatches('/profiles/{userId}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Profile associated to the ID."}

Scenario: pathMatches('/communities/{id}') && methodIs('get') && communities[pathParams.id] != null
    * def response = communities[pathParams.id]

Scenario: pathMatches('/communities/{id}') && methodIs('get')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Community associated to the ID."}

Scenario: pathMatches('/communities') && methodIs('post') && toCommunity(karate.pretty(request)) == null
    * def responseStatus = 400
    * def response = {"code":"bad_community","message":"Bad community to store."}

Scenario: pathMatches('/communities') && methodIs('post') && request.id == null
	* def community = request
	* community.id = uuid()
	* communities[community.id] = community;
    * def response = community
    * def responseStatus = 201

Scenario: pathMatches('/communities') && methodIs('post') && communities[request.id] != null
    * def responseStatus = 400
    * def response = {"code":"bad_community","message":"Community already registered."}

Scenario: pathMatches('/communities') && methodIs('post')
	* def community = request
	* communities[community.id] = community;
    * def response = community
    * def responseStatus = 201

Scenario: pathMatches('/communities/{id}') && methodIs('delete') && communities[pathParams.id] != null
    * karate.remove('communities', '$.' + pathParams.id)
    * def responseStatus = 204

Scenario: pathMatches('/communities/{id}') && methodIs('delete')
    * def responseStatus = 404
    * def response = {"code":"not_found","message":"No Community associated to the ID."}

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
