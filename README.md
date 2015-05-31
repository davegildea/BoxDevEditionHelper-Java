## Instructions
In order to run the BoxDevEditionHelperClientExample make sure you copy the config.properties.example file to config.properties and change the properties to reflect your app's settings.

To use in your own project, currently the best way is to copy the BoxDevEditionHelper class into your project and make sure you include the following dependencies (Maven example):

```maven
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

Have a look at the example client to see how to interact with the helper class.  If you have questions please email!
