package amity;

import tetris.AI;
import tetris.Board;
import tetris.Move;
import tetris.Piece;
import AIHelper.*;
/**
  *Author: Kelvin Zhang & Everyone in my team
  *My design is loop through the next piece as well,
  *I generate those coefficients using a finder.
  *
  *
*/

public class AmityAI2 implements AI {
	FinalRater boardRater = new FinalRater();
	public double[] coefficients = { 0.41430724103382527, 0.04413383739389207,
			0.1420172532064692, -0.13881428312611474, 0.22970827267905328,
			-0.052368130931930074, 0.5712789822642919, 0.2851778629665227,
			0.041534211381371554, -0.011738293785449829, 0.241299661945633,
			0, // 0.8292064267563932, i put zero here because the AverageSquare rater is wrong so cancel it out by put a zero here.
			-0.009937763420971586 };
	public static BoardRater myrater[] = 
	{ new ConsecHorzHoles(), 
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
		new AverageSquaredTroughHeight(),
		new BlocksAboveHoles() };

	public AmityAI2() {
		boardRater.coefficients = this.coefficients; //set coefficients
		FinalRater.raters = AmityAI2.myrater; //Just reset the order of the raters, make sure it correspond to the right coefficients.
	}

	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
                double bestScore = 1e20;
                int bestX = 0;
                int bestY = 0;
                Piece bestPiece = piece;
                
                Piece current = piece;
                Piece next = nextPiece;

                // loop through all the rotations for current piece
                do {
                        final int yBound = limitHeight - current.getHeight()+1;
                        final int xBound = board.getWidth() - current.getWidth()+1;

                        // For current rotation, try all the possible columns
                        for (int x = 0; x<xBound; x++) {
                                int y = board.dropHeight(current, x);
                                if ((y<yBound) && board.canPlace(current, x, y)) {
                                        Board testBoard = new Board(board);
                                        testBoard.place(current, x, y);
                                        testBoard.clearRows();

                                                  // Embed another loop does the same thing to nextPiece,
                                                do
                                                {
                                                        final int jBound = limitHeight - next.getHeight()+1;
                                                        final int iBound = testBoard.getWidth() - next.getWidth()+1;
                                                        
                                                        for(int i = 0; i < iBound; i++)//for next piece, try out all the location.
                                                        {
                                                                int j = testBoard.dropHeight(next, i);
                                                                if(j < jBound && testBoard.canPlace(next, i, j)) {
                                                                        Board temp = new Board(testBoard);
                                                                        temp.place(next, i, j);
                                                                        temp.clearRows();
                                                                                
                                                                                double nextScore = boardRater.rateBoard(temp);
                                                                                score += this.rate(temp);//use my own rater to rate the board.
                                                                                if(nextScore < bestScore)
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
                                                // Back out to the current piece

                                        }
                                }
                        current = current.nextRotation();
                } while (current != piece);

                Move move = new Move();
                move.x=bestX;
                move.y=bestY;
                move.piece=bestPiece;
                return(move);
	}

	public double rate(Board board) {
		int w = board.getWidth();
		int[] troughs = new int[w];
		int x = 0, temp, temp2, temp3;
		troughs[0] = ((temp = board.getColumnHeight(1)
				- board.getColumnHeight(0)) > 0) ? temp : 0;
		for (x = 1; x < w - 1; x++) {
			troughs[x] = (temp = (((temp2 = (board.getColumnHeight(x + 1) - board
					.getColumnHeight(x))) > (temp3 = (board
					.getColumnHeight(x - 1) - board.getColumnHeight(x)))) ? temp3
					: temp2)) > 0 ? temp : 0;
		}
		troughs[w - 1] = ((temp = board.getColumnHeight(w - 2)
				- board.getColumnHeight(w - 1)) > 0) ? temp : 0;
		double average = 0.0;
		for (x = 0; x < w; x++)
			average += troughs[x] * troughs[x];
		return average / w * 0.8292064267563932;
	}

	public void setRater(BoardRater r) {
		return; // does not support yet!
	}

	public String toString() {
		return "AmityAi2";
	}

}