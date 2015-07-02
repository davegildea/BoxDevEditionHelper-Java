## Instructions
In order to run the BoxDevEditionHelperClientExample make sure you copy the config.properties.example file to config.properties and change the properties to reflect your app's settings.

To use in your own project, currently the best way is to copy the BoxDevEditionHelper class into your project and make sure you include the following dependencies (Maven example):

```maven
	<dependency>
	    <groupId>com.box</groupId>
	    <artifactId>box-java-sdk</artifactId>
	    <version>1.0.0</version>
	</dependency>
	<dependency>
	    <groupId>org.bitbucket.b_c</groupId>
	    <artifactId>jose4j</artifactId>
	    <version>0.4.2</version>
	</dependency>
	<dependency>
		<groupId>org.bouncycastle</groupId>
		<artifactId>bcprov-jdk15on</artifactId>
		<version>1.52</version>
	</dependency>
	<dependency>
		<groupId>org.bouncycastle</groupId>
		<artifactId>bcpkix-jdk15on</artifactId>
		<version>1.52</version>
	</dependency>
	<dependency>
	    <groupId>com.mashape.unirest</groupId>
	    <artifactId>unirest-java</artifactId>
	    <version>1.4.5</version>
	</dependency>
```
###Important!
Make sure you install the unrestricted encryption libraries for your JVM (if you don't you'll get an exception about key length).  This is not a Box thing, this is a U.S. Government requirement concerning strong encryption:

http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
