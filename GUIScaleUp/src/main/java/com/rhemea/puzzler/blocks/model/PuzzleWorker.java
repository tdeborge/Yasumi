package com.rhemea.puzzler.blocks.model;

import java.util.ArrayList;

import com.rhemea.puzzler.blocks.gui.IPuzzleCallBack;

public class PuzzleWorker implements Runnable {

	private int xPos, yPos;
	private BlockContainer pBox = null;
	private Grid pGrid = null;
	private ArrayList<char[][]> solutions = null;
	IPuzzleCallBack pcb;

	public PuzzleWorker(int x, int y, IPuzzleCallBack cb) {
		xPos = x;
		yPos = y;
		pBox = new BlockContainer();
		pGrid = new Grid();
		solutions = new ArrayList<char[][]>();
		pcb = cb;
	}

	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("PuzzleWorker:\n");
		bf.append("X = " + xPos + "\n");
		bf.append("Y = " + yPos + "\n");
		return bf.toString();
	}

	public ArrayList<char[][]> getSolutions() {
		return solutions;
	}
	
	public BlockContainer getBloksFromWorker(){
		return pBox;
	}

	@Override
	public void run() {
		int quadrant = 1;
		Block bl = pBox.getBlock(0);
		// Mark block as used
		bl.setUsed(true);
		for (int k = 0; k < bl.getNumberOfShapes(); k++) {
			char[][] grid = new char[pGrid.getGridY()][pGrid.getGridX()];
			pGrid.initGrid(grid);
			if (pGrid.placeBlockOnGrid(grid, bl, k, xPos, yPos, 0)) {
				bl.getShape(k).shapePlaced();
				placeNextBlockOnGrid(grid, quadrant);
			}
		}

		// if (this.pcb != null) {
		// try {
		// javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
		// public void run() {
		// pcb.beginBlokNr(counter++);
		// }
		// });
		// } catch (Exception e) {
		// }
		// }
		// //Return the block back to it's container
		// bl.setUsed(false);
		// try {
		// Thread.sleep(1000);
		// } catch (Exception e) {
		// }
	}

	public void placeNextBlockOnGrid(char[][] grid, int quadrant) {

		// find next available block
		int px = 0;
		int py = 0;

		// find next free block to try.
		if (quadrant == Integer.parseInt(ResourceLoader.getString("blocks.numbloks"))) {
			if (solutions.contains(grid)) {
				System.out.println("ALLERT:  DUPLICATE FOUND");
			} else {
				solutions.add(grid);
				// if (showSolution) {
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
				// }
				// pGrid.printCompleteSolutionForGrid(grid);
			}
			return;
		}

		// Checking for the first free cell in the grid to place the block.
		boolean cellfound = false;

		for (int quady = 0; quady < pGrid.getGridY(); quady++) {
			for (int quadx = 0; quadx < pGrid.getGridX(); quadx++) {
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

		// When a free cell is found, we need to try every free block on it.
		for (int bloknr = 0; bloknr < pBox.getNrOfBlocks(); bloknr++) {
			Block bl = pBox.getBlock(bloknr);
			if (bl.isUsed()) {
				continue;
			}
			// Here we have found a free block.
			// We need to mark it now as used
			bl.setUsed(true);

			// If we try a block, we need to try all the different shapes of
			// this block.
			for (int k = 0; k < bl.getNumberOfShapes(); k++) {
				char[][] mp = bl.getShapeMap(k);
				// If the first cell of the block is not empty, we can try the
				// block
				if (mp[0][0] != '0') {
					char[][] grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2, bl, k, px, py, bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid2, quadrant + 1);
					}
				} else {
					// if the first cell of a block is empty, it is useless to
					// try it on the exact location
					// because in that way we are generating gaps that never
					// will be filled. The solution
					// is to shift the block horizontally to the left until a
					// cell is occupied and afterwards
					// we shif the cell up to the top and calculate the same
					int i = 0;
					for (i = 0; i < bl.getShapeHeight(k); i++) {
						if (mp[i][0] != '0') {
							break;
						}
					}
					char[][] grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2, bl, k, px, py - i, bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid2, quadrant + 1);
					}

					int j = 0;
					for (j = 0; j < bl.getShapeWidth(k); j++) {
						if (mp[0][j] != '0') {
							break;
						}
					}
					grid2 = pGrid.copyGridmap(grid);
					if (pGrid.placeBlockOnGrid(grid2, bl, k, px - j, py, bloknr)) {
						bl.getShape(k).shapePlaced();
						placeNextBlockOnGrid(grid2, quadrant + 1);
					}
				}
			}
			bl.setUsed(false);
		}

	}

}
