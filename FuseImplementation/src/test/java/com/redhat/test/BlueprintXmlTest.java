package com.redhat.test;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class BlueprintXmlTest extends CamelBlueprintTestSupport {

	@Test
	public void testCamelRoute() throws Exception {
		getMockEndpoint("mock:catchMessage").expectedMinimumMessageCount(1);
		// Define some expectations

		// For now, let's just wait for some messages// TODO Add some expectations here

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml";
	}

}
