Feature: Mock server for the WeNet social context builder

Background:
	* configure cors = true

Scenario: pathMatches('/social/relations/{userId}') && methodIs('get')
    * def response = []

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
