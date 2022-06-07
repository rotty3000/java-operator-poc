package com.github.rotty3000.operator.internal;

import com.github.rotty3000.operator.ResourceHandler;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedInformerEventListener;

class ResourceHandlerWrapper<R extends HasMetadata> implements ResourceEventHandler<R>, SharedInformerEventListener {

	public ResourceHandlerWrapper(ResourceHandler<R> resourceHandler) {
		this.resourceHandler = resourceHandler;
	}

	@Override
	public void onAdd(R resource) {
		resourceHandler.onAdd(resource);
	}

	@Override
	public void onDelete(R resource, boolean deletedFinalStateUnknown) {
		resourceHandler.onDelete(null, deletedFinalStateUnknown);
	}

	@Override
	public void onException(Exception exception) {
		resourceHandler.onException(exception);
	}

	@Override
	public void onUpdate(R oldResource, R newResource) {
		resourceHandler.onUpdate(oldResource, newResource);
	}

	private final ResourceHandler<R> resourceHandler;

}