Feature: Mock server for the WeNet interaction protocol engine

Background:
	* configure cors = true

Scenario: pathMatches('/messages') && methodIs('post')
    * def response = request

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
