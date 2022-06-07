package com.github.rotty3000.operator.internal;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import com.github.rotty3000.operator.ResourceHandler;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;

public class Operator<T extends HasMetadata> implements Closeable {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Operator(Config config, String labelSelector, ResourceHandler<T> resourceHandler) {
		ParameterizedType pt = (ParameterizedType)resourceHandler.getClass().getGenericInterfaces()[0];
		Class<T> resourceType = (Class)pt.getActualTypeArguments()[0];

		kubernetesClient = new DefaultKubernetesClient(config);
		sharedInformerFactory = kubernetesClient.informers();

		ResourceHandlerWrapper<T> wrapper = new ResourceHandlerWrapper<>(resourceHandler);

		sharedInformerFactory.addSharedInformerEventListener(wrapper);

		kubernetesClient.resources(
			resourceType
		).withLabel(
			labelSelector
		).inform(
			wrapper
		);

		sharedInformerFactory.startAllRegisteredInformers();
	}

	@Override
	public void close() throws IOException {
		sharedInformerFactory.stopAllRegisteredInformers(true);
		kubernetesClient.close();
	}

	private final KubernetesClient kubernetesClient;
	private final SharedInformerFactory sharedInformerFactory;

}