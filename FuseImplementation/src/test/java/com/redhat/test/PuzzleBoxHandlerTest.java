package com.redhat.test;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import com.rhworkshop.msa.controller.PuzzleBox;

public class PuzzleBoxHandlerTest extends CamelBlueprintTestSupport {

	 @Produce(uri = "box.PuzzlerJMSConnection:queue:{{Puzzler.PuzzleBox.Destination}}")
	    protected ProducerTemplate template;
	 
		@Override
		public void setUp() throws Exception {
			// TODO Auto-generated method stub
			super.setUp();
			
			context.addRoutes(new RouteBuilder(){
				 @Override
		            public void configure() throws Exception
		            {
					 	from("box.PuzzlerJMSConnection:queue:{{Puzzler.Forward.Destination}}")
					 	.to("mock:jmsReceiver");
		            }
			});
			
		}
	
	@Test
	public void testPuzzleBoxInitialization() throws Exception {
		
		template.sendBody(new PuzzleBox(0,0));
		template.sendBody(new PuzzleBox(1,1));
		getMockEndpoint("mock:catchMessage").expectedMinimumMessageCount(2);
		getMockEndpoint("mock:jmsReceiver").expectedMinimumMessageCount(1);
		// Define some expectations

		// For now, let's just wait for some messages// TODO Add some expectations here

		// Validate our expectations
		assertMockEndpointsSatisfied();
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/FuseYasumiPuzzleBoxHandler.xml";
	}
//OSGI-INF/blueprint/FuseYasumiPuzzler.xml,
	//box.PuzzlerJMSConnection:queue:{{Puzzler.PuzzleBox.Destination}
}
