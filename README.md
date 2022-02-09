[![Build and Test](https://github.com/InternetOfUs/common-models-java/actions/workflows/branch-build-and-test.yml/badge.svg?branch=develop)](https://github.com/InternetOfUs/common-models-java/actions/workflows/branch-build-and-test.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Internet of us - Common models java

This project contains some common java code that can be used by the components.
It is formed by the next modules:

 * __common-bom__  Bill of materials of the project. This contains all the modules of the project.
 * __common-test__  Project with the utilities and dependencies to do tests.
 * __common-model__  Basic classes used to define teh internet of us data model.
 * __common-vertx__  Utility classes to define microservices to provide the component web services.
 * __common-components__  The clients to interact with the platform components.
 * __common-protocols__  Define some protocols that describe the used interactions on the pilot tasks.
 * __common-dummy__  Dummy implementations to test the modules.


## Use this common component in another project

You will need to add the below to your **pom.xml** file.

```maven
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
...
 <properties>
  ...
  <eu.internetofus.common-version>1.0.0</eu.internetofus.common-version>
  ...
 </properties>
 <repositories>
 ...
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/InternetOfUs/common-models-java</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
  ...
  </repositories>
  ...
 <dependencyManagement>
  <dependencies>
  ...
   <dependency>
    <groupId>eu.internetofus</groupId>
    <artifactId>common-bom</artifactId>
    <version>${eu.internetofus.common-version}</version>
    <type>pom</type>
    <scope>import</scope>
   </dependency>
  ...
  </dependencies>
 </dependencyManagement>
 ...
</project>
```

## Development

First of all, you must install [docker](https://docs.docker.com/install/).
After that you can start a development environment with the script
**startDevelopmentEnvironment.sh**. It creates a docker image with
the software to compile and test the project.

This project uses the [Apache maven](https://maven.apache.org/) to solve
the dependencies, compile and run the test.

 - Use `mvn compile` to compile and generate the Open API documentation (**target/classes/wenet-profile_manager-openapi.yml**).
 - Use `mvn test` to run the test. Exist some Integration test that requires around 10 minutes, so if you want to ignore them execute them with `mvn -Ddisable.large.unit.tests=true test`.
 - Use `mvnd test` to run the test on debug mode.
 - Use `mvn site` to generate a HTML page (**target/site/index.html**) with all the reports (test, javadoc, PMD,CPD and coverage).


When you finish you can **exit** the bash or stop the started docker container
with the script **stopDevelopmentEnvironment.sh**.


### Deployment

This project is automatically deployed every time you create a tag on the repository.
If you want you can manually deploy with the command `mvn deploy`,
but before you must set the deploy token into the maven settings. The next steps
explains how to do it.

 * Go to [Github tokens](https://github.com/settings/tokens)
 * Generate new token, with the privilege __write:packages__.
 * In your host create the file if not exist **$HOME/.m2/settings.xml**.
 * And add the next lines to it, replacing **SECRET_TOKEN** by the generated token
 and **GITHUB_USER_ID** with your github identifier.

 ```xml
 <settings>
  <servers>
    <server>
      <id>github</id>
      <username>GITHUB_USER_ID</username>
	  <password>SECRET_TOKEN</password>
    </server>
  </servers>
</settings>
 ```

## License

This software is under the [Apache V2 license](LICENSE)

## Contact

### Researcher

 - [Nardine Osman](http://www.iiia.csic.es/~nardine/) ( [IIIA-CSIC](https://www.iiia.csic.es/~nardine/) ) nardine (at) iiia.csic.es
 - [Carles Sierra](http://www.iiia.csic.es/~sierra/) ( [IIIA-CSIC](https://www.iiia.csic.es/~sierra/) ) sierra (at) iiia.csic.es

### Developers

 - Joan Jené ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=19) ) jjene (at) iiia.csic.es
 - Bruno Rosell i Gui ( [UDT-IA, IIIA-CSIC](https://www.iiia.csic.es/people/person/?person_id=27) ) rosell (at) iiia.csic.es
