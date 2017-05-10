package com.redhat.test;



import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class YasumiPuzzlerStartTest extends CamelBlueprintTestSupport {

	 @Produce(uri = "activemq:queue:localhost.test.yasumi.start?username=admin&password=admin")
	    protected ProducerTemplate template;
	
	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		context.addRoutes(new RouteBuilder(){
			 @Override
	            public void configure() throws Exception
	            {
				 	from("PuzzlerJMSConnection:queue:{{Puzzler.PuzzleBox.Destination}}")
				 	.to("mock:jmsReceiver");
	            }
		});
		
	}
	@Test
	public void testYasumiInitialisation() throws Exception {
		
		template.sendBody("Hello From Unit Test");
		
		getMockEndpoint("mock:jmsReceiver").expectedMinimumMessageCount(8);
		// Define some expectations

		// For now, let's just wait for some messages// TODO Add some expectations here

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/FuseYasumiPuzzler.xml";
	}

}
