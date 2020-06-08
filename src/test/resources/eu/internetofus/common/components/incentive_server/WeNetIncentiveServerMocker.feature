Feature: Mock server for the WeNet incentive server

Background:
	* configure cors = true

Scenario: pathMatches('/Tasks/TaskStatus/') && methodIs('post')
    * def response = request

Scenario:
    * def responseStatus = 501
    * def response = {"code":"not_implemented","message":"Sorry, This API call is not mocked."}
