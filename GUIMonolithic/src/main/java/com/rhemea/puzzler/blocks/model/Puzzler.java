package com.rhemea.puzzler.blocks.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import com.rhemea.puzzler.blocks.gui.IPuzzleCallBack;


/*
It is possible to do a no-think program and let it run for a very long time. I have tried to implement
the following optimalisations:

1) Because all figures are different, one solution actually represents 4 solutions. It is also a horizontal,
vertical and diagonal mirror that has the solution.

2) Because of reason 1, I wanted to minimize the number of iterations in the calculations. Looking at the blocks,
I foud that the block with the cross is not rotatable. You always end up with the same figure ..... For that 
reason, I took this block and placed it in all possible ways in the first quadrant (avoiding a mirrored image
of this block). If I take that block + all the other combinations of all blocks, I can stop calculating afterwards
as I also have all mirrors in the solution.

3) It is important to know when to stop trying new blocks on the grid. As all blocks are builded out of 5 grid-cells,
you can stop placing new block if the number of consecutive free cells is not dividable by 5. This was a pointer I 
took from Jan Sipke van der Veen.  
*/



//This is the actual class that performs the actions and hopefully comes to a lot of solutions
public class Puzzler implements Runnable {

	private BlockContainer 	pBox = null;
	private Grid 			pGrid = null;
	private boolean			showSolution = false;
	private int counter;
	
	private ArrayList<char[][]>		solutions = null;
	
	private IPuzzleCallBack pcb = null;
	
//	private ResourceBundle bundle = null;
	private String solutionName = "lastCheck";	
//	boolean sol = false;
	
	public Puzzler() {
		//Initializing the playfield and puzzle blocks for the Puzzler
		pBox = new BlockContainer();
		pGrid = new Grid(); 
	}
	
	public Puzzler(IPuzzleCallBack thePcb) {
		this();		
		pcb = thePcb;
	}
	
	public void run() {
		counter = 0;
		solutions = new ArrayList<char[][]>();
		int quadrant = 1;
		for (int i = 0; i < pGrid.getYPos();i++ ) {
			for (int j = 0; j < pGrid.getXPos(); j++ ) {

				
				Block bl = pBox.getBlock(0);
				//Mark block as used
				bl.setUsed(true);
				for (int k = 0; k < bl.getNumberOfShapes() ; k++ ) {
					char [][] grid = new char[pGrid.getGridY()][pGrid.getGridX()];
					pGrid.initGrid(grid);
					if (pGrid.placeBlockOnGrid(grid,bl, k, j, i,0)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid, quadrant);
					}
				}
				
				if (this.pcb != null) {
					try {
						javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								pcb.beginBlokNr(counter++);
							}
						});
					} catch (Exception e) {
					}
				}
				//Return the block back to it's container
				bl.setUsed(false);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				
			}
		}
		this.writeSolutionHolderObject(solutions);
		
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					pcb.setResult(solutions);
					pcb.beginBlokNr(999);
				}
			});
		} catch (Exception e) {
		}
		
		for (int i= 0; i < pBox.getNrOfBlocks() ; i++ ) {
			Block b = pBox.getBlock(i);
			for (int j = 0; j < b.getNumberOfShapes() ; j++ ) {
				System.out.println("Block " + i + "  number of times shape " + j + " placed = " + b.getShape(j).getShapePlaced());
			}
		}
		
	}
	
	public static void main(String args[]) {
		System.out.println("Starting the Puzzler");
		Puzzler puz = new Puzzler();
		if (args.length == 1) {
			if (args[0].equals("new")) {
				puz.initSolutions();
			}
			else {
				puz.readSolutions();
			}
		}
		else {
			puz.readSolutions();
		}
		puz.go();
	}
	
	public void initSolutions() {
		solutions = new ArrayList<char[][]>();
	}
	
	@SuppressWarnings("unchecked")
	public void readSolutions() {
		solutions = (ArrayList<char[][]>) this.readSolutionHolderObject();		
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<char[][]> getSolutionArray(boolean newList) {
		if (newList) {
			solutions = new ArrayList<char[][]>();
		}
		else {
			solutions = (ArrayList<char[][]>) this.readSolutionHolderObject();		
		}
		return(solutions);
	}
	
	public void go() {
		System.out.println("go .. go .. go");
		
		if (solutions.size() == 0) {
			System.out.println("We are constructing a new list !!!!\n");

			Calendar rightNow = Calendar.getInstance();			
			
			System.out.println("The Program started on: " + rightNow.get(Calendar.DAY_OF_MONTH) +
															"/" +
															(rightNow.get(Calendar.MONTH) +1 ) +
															"/" +
															rightNow.get(Calendar.YEAR) +
															" at " +
															rightNow.get(Calendar.HOUR_OF_DAY) + 
															":" +
															rightNow.get(Calendar.MINUTE) + 
															":" +
															rightNow.get(Calendar.SECOND) + "\n\n\n"
															);
			System.out.println("PLEASE WAIT UNTIL ALL SOLUTIONS ARE FOUND!!!!!!");
			System.out.println("\nThis takes about 15 minutes on a PIII 600 running W2000");
															
			int quadrant = 1;
			for (int i = 0; i < pGrid.getYPos();i++ ) {
				for (int j = 0; j < pGrid.getXPos(); j++ ) {
					Block bl = pBox.getBlock(0);
					//Mark block as used
					bl.setUsed(true);
					for (int k = 0; k < bl.getNumberOfShapes() ; k++ ) {
						char [][] grid = new char[pGrid.getGridY()][pGrid.getGridX()];
						pGrid.initGrid(grid);
						if (pGrid.placeBlockOnGrid(grid,bl, k, j, i,0)) {
	//						pGrid.showGrid(grid);
							bl.getShape(k).shapePlaced();
							placeNextBlockOnGrid(grid, quadrant);
						}
					}
					//Return the block back to it's container
					bl.setUsed(false);
				}
			}
			
			System.out.println("The puzzler found " + solutions.size() + " Possible combinations.");
			System.out.println("In total there are " + (solutions.size() * 4) + " possble combinations !!!");
			
			rightNow = Calendar.getInstance();
			System.out.println("The Program ended on: " + rightNow.get(Calendar.DAY_OF_MONTH) +
															"/" +
															(rightNow.get(Calendar.MONTH) +1 ) +
															"/" +
															rightNow.get(Calendar.YEAR) +
															" at " +
															rightNow.get(Calendar.HOUR_OF_DAY) + 
															":" +
															rightNow.get(Calendar.MINUTE) + 
															":" +
															rightNow.get(Calendar.SECOND)
															);
														
			System.out.println("All found solutions:");
			this.writeSolutionHolderObject(solutions);
		}
		else {
			System.out.println("Printing the results from the last search ....\n\n\n");
		}
		for (int i = 0; i < solutions.size() ; i++ ) {
			pGrid.printCompleteSolutionForGrid((char[][])solutions.get(i));
		}
	}
	
	public void placeNextBlockOnGrid(char[][]grid, int quadrant) {
	
		//find next available block
		int px = 0;
		int py = 0;
		
		//find next free block to try.
		if (quadrant == Integer.parseInt(ResourceLoader.getString("blocks.numbloks"))) {
			if (solutions.contains(grid)) {
				System.out.println("ALLERT:  DUPLICATE FOUND");
			}
			else {
				solutions.add(grid);
				pGrid.showGrid(grid);
				if (showSolution) {
					if (this.pcb != null) {
					try {
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								pcb.setLastResult(solutions);
							}
						});
					} catch (Exception e) {
					}
				}	
				}
//				pGrid.printCompleteSolutionForGrid(grid);
			}
			return;
		}		
		
		//Checking for the first free cell in the grid to place the block.		
		boolean cellfound = false;
		
		for (int quady = 0; quady < pGrid.getGridY(); quady++ ) {
			for (int quadx = 0; quadx < pGrid.getGridX(); quadx++ ) {
				if (grid[quady][quadx] == '0') {
					cellfound = true;
					px = quadx;
					py = quady;
					break;
				}
			}
			if (cellfound) {
				break;
			}	
		}
		
	
		//When a free cell is found, we need to try every free block on it.
		for (int bloknr = 0; bloknr < pBox.getNrOfBlocks() ; bloknr++ ) {
			Block bl = pBox.getBlock(bloknr);
			if (bl.isUsed()) {
				continue;				
			}
			//Here we have found a free block.
			//We need to mark it now as used				
			bl.setUsed(true);
			
			//If we try a block, we need to try all the different shapes of this block.
			for (int k = 0; k < bl.getNumberOfShapes() ; k++ ) {
				char[][] mp = bl.getShapeMap(k);
				//If the first cell of the block is not empty, we can try the block
				if (mp[0][0] != '0') {
					char[][] grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2,bl, k, px, py, bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid( grid2,quadrant + 1);
					}
				}
				else {
					//if the first cell of a block is empty, it is useless to try it on the exact location
					//because in that way we are generating gaps that never will be filled. The solution
					//is to shift the block horizontally to the left until a cell is occupied and afterwards
					//we shif the cell up to the top and calculate the same 
					int i = 0;
					for (i = 0; i < bl.getShapeHeight(k) ; i++) {
						if (mp[i][0] != '0') {
							break;
						}
					}
					char[][] grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2,bl, k, px, py -i, bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid2, quadrant + 1);
					}
					
					int j = 0;		
					for (j = 0; j < bl.getShapeWidth(k) ; j++) {
						if (mp[0][j] != '0') {
							break;
						}
					}
					grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2,bl, k, px - j, py , bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid2,quadrant + 1);
					}								
				}
			}
			bl.setUsed(false);
		}
		
	}
	
	
	private void writeSolutionHolderObject(Object obj) {
		try {
			FileOutputStream out = new FileOutputStream(solutionName);
			ObjectOutputStream s = new ObjectOutputStream(out);
			s.writeObject(obj);
			s.close();
			out.close();
		} catch (Exception e) {
			System.out.println("Error writing Object " + obj + "\n Message = " + e.getMessage());
			System.err.println("Error writing Object " + obj + "\n Message = " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Object readSolutionHolderObject() {
		try{
		
			File test = new File(solutionName);
			if (! test.exists()) {
				Object ret = new ArrayList<char[][]>();
				return(ret);
				
			}
			java.io.FileInputStream fis = new java.io.FileInputStream(test);
			java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
			Object readObj = ois.readObject();
			fis.close();
			return readObj;
		}
		catch(Exception e) { 
			System.out.println("Error reading Object " + solutionName + "\n Message = " + e.getMessage());
			System.err.println("Error reading Object " + solutionName + "\n Message = " + e.getMessage());
			e.printStackTrace();		
		} // return null
		
		return null;
	}
	
	public void setInteractive(boolean showit) {
		showSolution = showit;
	}
}