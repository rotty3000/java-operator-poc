package com.github.rotty3000.operator;

import io.fabric8.kubernetes.api.model.HasMetadata;

public interface ResourceHandler<R extends HasMetadata> {

	public void onAdd(R resource) ;

	public void onDelete(R resource, boolean deletedFinalStateUnknown);

	public void onException(Exception exception);

	public void onUpdate(R oldResource, R newResource);

}