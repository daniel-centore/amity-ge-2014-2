package amity;


import tetris.*;
import AIHelper.*;

/**
 * @author Kelvin Zhang
 * My algorithm is pretty much the same as the given one.
 * I enhanced it a little bit by counting the next piece as well.
 * I added another loop to do the same calculation to next piece;
 * it's a little bit slower but can do more moves.
 * 
 * 
 */
public class AmityAI2 implements AI {

	FinalRater boardRater = new FinalRater();
	//coefficients
	
	
	
	  public double[] coefficients = { //You don't need those D at the end of each number, I put them there just to remind me they are doubles.
			  0.4143072410338253D,
			  0.04413383739389207D,
			  0.1420172532064692D, 
			  -0.1388142831261147D,
			  0.2297082726790533D, 
			  -0.05236813093193007D, 
			  0.5712789822642919D,
			  0.285177862966523D, 
			  0.04153421138137155D, 
			  -0.01173829378544983D, 
			  0.241299661945633D, 
			  0,//0.8292064267563932D is the true coefficients, I put 0 here to eliminate the wrong AverageSquaredTroughHeight Rater
			  -0.009937763420971586D, 
			  };




	public static BoardRater myrater[] = // staticness prevents these raters
											// from getting instantiated over
											// and over and over and over
											// again... this'll save garbage
											// collection time.
	{ 
		new ConsecHorzHoles(), 
		new HeightAvg(), 
		new HeightMax(),
		new HeightMinMax(), 
		new HeightVar(), 
		new HeightStdDev(),
		new SimpleHoles(), 
		new ThreeVariance(), 
		new Through(),
		new WeightedHoles(), 
		new RowsWithHolesInMostHoledColumn(),
		new AverageSquaredTroughHeight(),//I put this rater here even I know it's wrong because I use the coefficient 0 to eliminate it.
		new BlocksAboveHoles() 
	};
	/**
	 * Do some set ups.
	 */
	public AmityAI2() {
		boardRater.coefficients = this.coefficients;//set the coefficients
		FinalRater.raters = AmityAI2.myrater;//just to make sure those coefficients correspond to the right raters.
	}
	
	/**
	 * Reason I use do-while loop instead of for loop or while loop is because 
	 * I want to stop looping when the piece gets back to its original rotation
	 * since some pieces have to rotate. Some also have 4 and I have to consider that.
	 * We could use for(int i = 0; i < 4; i ++), but that's going to rotate the 
	 * piece 4 times for all pieces, sometimes it does extra work, and I want it to be more efficient.
	 * Also for the first rotation (the original piece), I use the do-while loop to make sure it rotates 
	 * at least one time. 
	 */
	
	
	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
		double bestScore = Double.MAX_VALUE; //worse score you could get..
		int bestX = 0; //best location 
		int bestY = 0;
		Piece bestPiece = piece; //best rotation so far
		
		Piece current = piece; //i store those piece because i want to stop the loop when the piece
		Piece next = nextPiece;//is back to it's original rotation.

		// rotate the piece until it backs to original rotation
		do {
			final int yBound = limitHeight - current.getHeight() + 1;
			final int xBound = board.getWidth() - current.getWidth() + 1;

			// For current rotation, try all the possible value of y or all the possible COLs,. 
			for (int x = 0; x<xBound; x++) {
				int y = board.dropHeight(current, x);
				if ((y<yBound) && board.canPlace(current, x, y)) { //Even though place() method check call canPlace() method for us to check
					//I still put canPlace() here because if it returns an false, we will the whole inner do-while loop which saves us a lot of time.
					Board testBoard = new Board(board);
					testBoard.place(current, x, y);
					testBoard.clearRows();
					
						//another do-while loop, just do the same calculation to next piece
						do
						{
							final int jBound = limitHeight - next.getHeight() + 1;
							final int iBound = testBoard.getWidth() - next.getWidth() + 1;
							
							for(int i = 0; i < iBound; i++)
							{
								int j = testBoard.dropHeight(next, i);
								
								if(j < jBound && testBoard.canPlace(next, i, j)) {
									Board temp = new Board(testBoard);
									temp.place(next, i, j);
									temp.clearRows();
										
										double nextScore = boardRater.rateBoard(temp);//rate the board
										nextScore += this.rate(temp); //Add the score of correct AverageSquaredTroughtHeight rater.
										
										if(nextScore < bestScore)//We find a better moves. For the score, the lower, the better
										{
											bestScore = nextScore;
											bestX = x;
											bestY = y;
											bestPiece = current;
										}
									}

								}

							next = next.nextRotation();
						} while (next != nextPiece);
						// Back to the original rotation, stop.

					}
				}
			current = current.nextRotation(); //rotate the piece
		} while (current != piece);	// Back to the original rotation, stop.


		Move move = new Move();
		move.x = bestX;
		move.y = bestY;
		move.piece = bestPiece;
		return(move);

	}
	
	
	/**
	 * My own rater, a replacement for AverageSquaredTroughHeight. The reason why I didn't create
	 * a separate class is because the rate() method from BoardRater is default, and we can't
	 * access that method from another package. So I created a method here.
	 * 
	 * 
	 * The following codes are found on google code:
	 * https://code.google.com/p/tetris-ai/
	 * 
	 * @param board
	 * @return
	 */
	public double rate(Board board) {
		int w = board.getWidth();
		int[] troughs = new int[w]; //Array for trough number of each wall.
		int x = 0, temp, temp2, temp3;
		
		troughs[0] = ((temp = board.getColumnHeight(1)//Get the height of right wall which is x + 1;
				- board.getColumnHeight(0)) > 0) ? temp : 0; //Subtract by the height of first wall.
		
		for (x = 1; x < w - 1; x++) {
			troughs[x] = (temp = (((temp2 = (board.getColumnHeight(x + 1) - board
					.getColumnHeight(x))) > (temp3 = (board
					.getColumnHeight(x - 1) - board.getColumnHeight(x)))) ? temp3
					: temp2)) > 0 ? temp : 0;
		}
		
		troughs[w - 1] = ((temp = board.getColumnHeight(w - 2)//Since the last wall has nothing on it's right, we have to do it separately.
				- board.getColumnHeight(w - 1)) > 0) ? temp : 0;
		
		double average = 0.0;
		for (x = 0; x < w; x++)
			average += troughs[x] * troughs[x]; // Squared them
		return average / w * 0.8292064267563932D; //Get the average and times the coefficient then return.
		
	}
	/**
	 * Not support yet, this method doesn't do anything.
	 * Don't call this method
	 */
	public void setRater(BoardRater r) { //I don't want other people to change my raters.
		return;
	}
	
/* No use at all, just testing.
	public String toString() {
		return "AmityAi2"; 
	}
	*/

}