## com.github.rotty3000.operator.java

A pattern for build a simple Java Kubernetes Operator with the smallest possible size.

To build run (including integration tests):

```bash
./mvnw clean verify
```

To then produce the docker image with JLinked JDK, run:

```bash
docker build \
	--build-arg EXECUTABLE_JAR=target/exec.jar \
	--build-arg MODULE_NAME=com.github.rotty3000.operator.java \
	--build-arg EXTRA_MODULES=jdk.jdwp.agent,jdk.unsupported \
	--pull --rm -f Dockerfile \
	-t <tag> .
```

### Altering the Operator's behaviour

Currently the operator is pretty dumb and merely echos ConfigMap events:

```java
public class HandlerImpl implements ResourceHandler<ConfigMap> {

	@Override
	public void onAdd(ConfigMap configMap) {
		logger.info("onAdd: {}", configMap);
	}

	@Override
	public void onDelete(
		ConfigMap configMap, boolean deletedFinalStateUnknown) {

		logger.info("onDelete: {}", configMap);
	}

	@Override
	public void onException(Exception exception) {
		logger.error("onException", exception);
	}

	@Override
	public void onUpdate(
		ConfigMap oldConfigMap, ConfigMap newConfigMap) {

		logger.info("onUpdate: from {}, to {}", oldConfigMap, newConfigMap);
	}

	private static Logger logger = LoggerFactory.getLogger(ConfigMapHandler.class);

}
```

This is the only class needs to be modified in the simple cases.

One only needs to alter this implementation adding logic to the event handler methods and/or by changing the resource type.