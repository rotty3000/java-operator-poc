package com.github.rotty3000.operator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import com.github.rotty3000.operator.component.OperatorComponent;
import com.github.rotty3000.operator.component.OperatorConfiguration;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

/**
 * Unit test for Operator.
 */
@EnableRuleMigrationSupport
public class OperatorTest {

	@Rule
	public KubernetesServer kubernetesServer = new KubernetesServer(false, true);

	@Test
	public void testOperatorComponent() throws Exception {
		KubernetesMockServer kubernetesMockServer =
			kubernetesServer.getKubernetesMockServer();

		OperatorConfiguration config = getConfiguration(kubernetesMockServer);

		MyHandler myHandler = new MyHandler(0);

		OperatorComponent operatorComponent = new OperatorComponent(
			config, myHandler);

		myHandler.await();

		Assertions.assertTrue(myHandler.getConfigMaps().isEmpty());

		operatorComponent.deactivate();
	}

	@Test
	public void testOperatorComponentDoAdd() throws Exception {
		KubernetesMockServer kubernetesMockServer =
			kubernetesServer.getKubernetesMockServer();

		OperatorConfiguration config = getConfiguration(kubernetesMockServer);

		MyHandler myHandler = new MyHandler(1);

		OperatorComponent operatorComponent = new OperatorComponent(
			config, myHandler);

		kubernetesMockServer.createClient().configMaps().createOrReplace(new ConfigMapBuilder().
			withNewMetadata().withName("foo").endMetadata().
			addToData("foo", "" + new Date()).
			addToData("bar", "beer").
			build());

		myHandler.await();

		Assertions.assertFalse(myHandler.getConfigMaps().isEmpty());

		operatorComponent.deactivate();
	}

	public static class MyHandler  implements ResourceHandler<ConfigMap> {

		private final List<ConfigMap> configMaps = new ArrayList<>();
		private final CountDownLatch latch;

		public MyHandler(int latchCount) {
			latch = new CountDownLatch(latchCount);
		}

		public void await() throws InterruptedException {
			latch.await(5, TimeUnit.SECONDS);
		}

		public List<ConfigMap> getConfigMaps() {
			return configMaps;
		}

		@Override
		public void onAdd(ConfigMap configMap) {
			configMaps.add(configMap);
			latch.countDown();
		}

		@Override
		public void onDelete(
			ConfigMap configMap, boolean deletedFinalStateUnknown) {

			configMaps.remove(configMap);
			latch.countDown();
		}

		@Override
		public void onException(Exception exception) {
		}

		@Override
		public void onUpdate(
			ConfigMap oldConfigMap, ConfigMap newConfigMap) {

			onDelete(oldConfigMap, false);
			onAdd(newConfigMap);
		}

	}

	private OperatorConfiguration getConfiguration(
		KubernetesMockServer kubernetesMockServer) {

		return new OperatorConfiguration() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return OperatorConfiguration.class;
			}

			@Override
			public String apiServerHost() {
				return kubernetesMockServer.getHostName();
			}

			@Override
			public int apiServerPort() {
				return kubernetesMockServer.getPort();
			}

			@Override
			public boolean apiServerSSL() {
				return false;
			}

			@Override
			public String caCertData() {
				return null;
			}

			@Override
			public String labelSelector() {
				return "";
			}

			@Override
			public String namespace() {
				return "test";
			}

			@Override
			public String saToken() {
				return null;
			}

		};
	}

}