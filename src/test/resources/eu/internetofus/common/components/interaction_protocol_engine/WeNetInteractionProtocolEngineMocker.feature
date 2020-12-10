Feature: Mock server for the WeNet interaction protocol engine

Background:
	* configure cors = true

Scenario: pathMatches('/messages') && methodIs('post')
    * def responseStatus = 202
    * def response = request

Scenario: pathMatches('/incentives') && methodIs('post')
    * def responseStatus = 202
    * def response = request

Scenario: pathMatches('/tasks/created') && methodIs('post')
    * def responseStatus = 202
    * def response = request

Scenario: pathMatches('/tasks/transactions') && methodIs('post')
    * def responseStatus = 202
    * def response = request

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
