package com.redhat.test;

import java.util.ArrayList;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import com.rhworkshop.msa.controller.PuzzleBox;
import com.rhworkshop.msa.model.Block;
import com.rhworkshop.msa.model.BlockContainer;
import com.rhworkshop.msa.model.Grid;

public class PuzzleBoxCalculatorTest extends CamelBlueprintTestSupport {

	 @Produce(uri = "calc.PuzzlerJMSConnection:queue:{{Puzzler.Forward.Destination}}")
	    protected ProducerTemplate template;
	 
		@Override
		public void setUp() throws Exception {
			// TODO Auto-generated method stub
			super.setUp();
			
//			context.addRoutes(new RouteBuilder(){
//				 @Override
//		            public void configure() throws Exception
//		            {
//					 	from("calc.PuzzlerJMSConnection:queue:{{Puzzler.Forward.Destination}}")
//					 	.to("mock:jmsReceiver");
//		            }
//			});
			
		}
	
	public PuzzleBox createInitBox(int x, int y){
		PuzzleBox pb = new PuzzleBox(x,y);
		//placing the cross on the grid and if valid, pass it on and set a valid message handle.
		Grid pGrid = pb.getpGrid();
		BlockContainer pBox = pb.getpBox();
		Block bl = pBox.getBlock(0);
		// Mark block as used
		bl.setUsed(true);
		for (int k = 0; k < bl.getNumberOfShapes(); k++) {
			char[][] grid = new char[pGrid.getGridY()][pGrid.getGridX()];
			pGrid.initGrid(grid);
			if (pGrid.placeBlockOnGrid(grid, bl, k, pb.getXgrid(), pb.getYgrid(), 0)) {
				pb.setGrid(grid);
				pb.setDepth(1);
				//exchange.getIn().setHeader("validMessage", true);
				//pGrid.showGrid(grid);
			}
			else{
				//exchange.getIn().setHeader("validMessage",false);
			}
		}
		pb.setPuzzleUnit("UnitTest");
		
		return pb;
	}
		
	@Test
	public void testPuzzleCalculatorInitialization() throws Exception {
		
		//put code here to simulate an initialized puzzlebox ...
		
		PuzzleBox pb = this.createInitBox(1, 1);
		PuzzleBox pb2 = this.createInitBox(3, 3);
		pb.setPuzzleUnit("UnitTest");
		pb2.setPuzzleUnit("UnitTest");
		
		
		template.sendBodyAndHeader(pb, "puzzledepth", 1);
		//template.sendBodyAndHeader(pb2, "puzzledepth", 1);
		
		//template.sendBody(pb);
		//template.sendBody(new PuzzleBox(1,1));
		getMockEndpoint("mock:catchMessage").expectedMinimumMessageCount(1);
		getMockEndpoint("mock:catchSplit").expectedMinimumMessageCount(8);
		getMockEndpoint("mock:catchForward").expectedMinimumMessageCount(1);

//		getMockEndpoint("mock:jmsReceiver").expectedMinimumMessageCount(1);
		// Define some expectations

		
		// For now, let's just wait for some messages// TODO Add some expectations here
	
		Thread.sleep(5000);
		// Validate our expectations
		assertMockEndpointsSatisfied();
	}
	

	
//	@Test
//	public void testFullCalculator() throws Exception {
//		
//		//put code here to simulate an initialized puzzlebox ...
//		
//		PuzzleBox pb = this.createInitBox(1, 1);
//		pb.setPuzzleUnit("UnitTest");
//		
//		ArrayList <PuzzleBox> al = new ArrayList<PuzzleBox>();
//		al.add(this.createInitBox(1, 0));
//		al.add(this.createInitBox(2, 0));
//		al.add(this.createInitBox(3, 0));
//		al.add(this.createInitBox(0, 1));
//		al.add(this.createInitBox(1, 1));
//		al.add(this.createInitBox(2, 1));
//		al.add(this.createInitBox(3, 1));
//
//		
//		
//		template.sendBody(al);
//		//template.sendBody(al);
//		//template.sendBody(new PuzzleBox(1,1));
//		getMockEndpoint("mock:catchMessage").expectedMinimumMessageCount(2);
////		getMockEndpoint("mock:jmsReceiver").expectedMinimumMessageCount(1);
//		// Define some expectations
//
//		// For now, let's just wait for some messages// TODO Add some expectations here
//		Thread.sleep(60000);
//		// Validate our expectations
//		assertMockEndpointsSatisfied();
//	}


	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/FuseYasumiPuzzleBoxCalculator.xml";
	}
//OSGI-INF/blueprint/FuseYasumiPuzzler.xml,
	//box.PuzzlerJMSConnection:queue:{{Puzzler.PuzzleBox.Destination}
}
