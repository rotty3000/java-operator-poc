package com.github.rotty3000.operator.component;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.github.rotty3000.operator.ResourceHandler;
import com.github.rotty3000.operator.internal.Operator;

import io.fabric8.kubernetes.client.Config;

@Component(
	configurationPid = "com.github.rotty3000.operator.OperatorConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class OperatorComponent {

	@Activate
	public OperatorComponent(
		OperatorConfiguration operatorConfiguration,
		@Reference ResourceHandler<?> resourceHandler) {

		_operator = new Operator<>(
			_toConfig(operatorConfiguration),
			operatorConfiguration.labelSelector(),
			resourceHandler);
	}

	@Deactivate
	public void deactivate() throws IOException {
		_operator.close();
	}

	private Config _toConfig(OperatorConfiguration operatorConfiguration) {
		Config config = Config.empty();

		Map<Integer, String> errorMessages = config.getErrorMessages();

		errorMessages.put(401, _ERROR_MESSAGE);
		errorMessages.put(403, _ERROR_MESSAGE);

		config.setCaCertData(operatorConfiguration.caCertData());

		config.setMasterUrl(
			Stream.of(
				operatorConfiguration.apiServerSSL() ? "https" : "http", "://",
				operatorConfiguration.apiServerHost(), ":", String.valueOf(
					operatorConfiguration.apiServerPort()
				), "/"
			).collect(joining())
		);

		config.setNamespace(operatorConfiguration.namespace());
		config.setOauthToken(operatorConfiguration.saToken());

		Config.configFromSysPropsOrEnvVars(config);

		return config;
	}

	private static final String _ERROR_MESSAGE =
		"Configured service account does not have access. Service account " +
			"may have been revoked.";

	private final Operator<?> _operator;

}