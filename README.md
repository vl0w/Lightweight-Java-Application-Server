[![Build Status](https://travis-ci.org/vl0w/Lightweight-Java-Application-Server.png?branch=dev)](https://travis-ci.org/vl0w/Lightweight-Java-Application-Server)

## Overview

LJAS is a small Java application server.

It was created back in 2010 for an educational project which was a little chat application like IRC, realized
by two developer trainees from Switzerland.  
As we designed the architecture, the project was strictly separated in the server engine
and the actual chat application code.   
For the sake of education, we didn't want to use existing JEE application servers as JBoss or Glassfish.


## Short Introduction

**Define an application interface**

```java

@LJASApplication(name = "MyApplication", version = "1.0")
public interface MyApplication extends Application {
	String upperCase(String thizz);
}
```

**Implement it (this gets executed on the server)**

```java

public class MyApplicationImpl extends ApplicationImplementation implements
		MyApplication {

	@Override
	public String upperCase(String thizz) {
		System.out.println("I'm gonna uppercase " + thizz);
		return thizz.toUpperCase();
	}
}
```

**Startup the server**

```java
public class MyServer {

	public static void main(String[] args) throws ConnectionRefusedException,
			Exception {
		Server server = new Server(new MyApplicationImpl());
		server.startup();
	}
}

```

**Connect a client**

```java

public class MyClient {

	public static void main(String[] args) throws Exception {
		Client client = new ClientImpl(MyApplication.class);
		client.connect("localhost", 7755);

		MyApplication application = client.getApplication();
		System.out.println(application.upperCase("Hello World"));
	}

}
```

**Result (as console output)**

![image](https://api.monosnap.com/image/download?id=0SZFlBXVGChAApBZJHfrclGcv)

![image](https://api.monosnap.com/image/download?id=5IE4LluZ2WbsD7SC6mBH7a1ck)
