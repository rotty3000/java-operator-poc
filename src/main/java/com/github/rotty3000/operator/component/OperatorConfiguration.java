package com.github.rotty3000.operator.component;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface OperatorConfiguration {

	String apiServerHost();

	int apiServerPort();

	boolean apiServerSSL();

	String caCertData();

	String labelSelector();

	String namespace();

	String saToken();

}
