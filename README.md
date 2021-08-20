# WeNet - Common

Common components used by the diferent UDT-IA, IIIA-CSIC components developed
for the project WeNet.


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
If you want you can manually deploy with the command `mvn -P iiia-deplo deploy`,
but before you must set the deploy token into the maven settings. The next steps
explains how to do it.

 * Go to gitlab inside the [Wenet Group](https://gitlab.iiia.csic.es/groups/internetofus/)
 * Go to [Settings->Repository setting](https://gitlab.iiia.csic.es/groups/internetofus/-/settings/repository/)
 * Expand the deploy token and create a new one with **read_package_registry**
 and **write_package_registry**.
 * In your host create the file if not exist **$HOME/.m2/settings.xml**.
 * And add the next lines to it, replacinf **DEPLOY_PASSWORD** by the provided
  by the created deploy token.
 
 ```xml
 <settings>
  <servers>
    <server>
      <id>gitlab-maven</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Deploy-Token</name>
            <value>DEPLOY_PASSWORD</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
</settings>
 ```
 
 You can read more of how to do it at the [gitlab documentation](https://docs.gitlab.com/ee/user/packages/maven_repository/#authenticate-to-the-package-registry-with-maven).

## Modules

 * __common-bom__  Bill of materials of the project. This contains all the modules of the project.
 * __common-test__  Project with the dependencies to do tests. 
 * __common-model__  Basic classes used to define a data model.
 * __common-vertx__  The classed to create the Verticle that manage the API.
 * __common-components__  The clients to interact with the WeNet components.
 * __common-protocols__  Define the common protocols to use.


## Use this common component in another project

You will need to add the below to your **pom.xml** file.

```maven
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
...
 <properties>
  ...
  <eu.internetofus.common-version>0.7.0</eu.internetofus.common-version>
 </properties>
 <repositories>
  <repository>
   <id>gitlab-maven</id>
   <url>https://gitlab.iiia.csic.es/api/v4/projects/443/packages/maven</url>
  </repository>
 </repositories>
 <distributionManagement>
  <repository>
   <id>gitlab-maven</id>
   <url>https://gitlab.iiia.csic.es/api/v4/projects/443/packages/maven</url>
  </repository>
  <snapshotRepository>
   <id>gitlab-maven</id>
   <url>https://gitlab.iiia.csic.es/api/v4/projects/443/packages/maven</url>
  </snapshotRepository>
 </distributionManagement>
 <dependencyManagement>
  <dependencies>
   <dependency>
    <groupId>eu.internetofus</groupId>
    <artifactId>common-bom</artifactId>
    <version>${eu.internetofus.common-version}</version>
    <type>pom</type>
    <scope>import</scope>
   </dependency>
  </dependencies>
 </dependencyManagement>
 ...
</project>
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
