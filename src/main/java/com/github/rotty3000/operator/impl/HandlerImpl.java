package com.github.rotty3000.operator.impl;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rotty3000.operator.ResourceHandler;

import io.fabric8.kubernetes.api.model.ConfigMap;

@Component
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

	private static Logger logger = LoggerFactory.getLogger(HandlerImpl.class);

}