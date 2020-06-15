Feature: Mock server for the WeNet social context builder

Background:
	* configure cors = true

Scenario: pathMatches('/social/relations/{userId}') && methodIs('get')
    * def response = []

Scenario: pathMatches('/social/preferences/{userId}/{taskId}') && methodIs('post')
    * def responseStatus = 200
    * def response = request

Scenario: pathMatches('/social/explanations/{userId}/{taskId}') && methodIs('get')
    * def responseStatus = 200
    * def response = {description:"Social description",Summary:{}}

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
