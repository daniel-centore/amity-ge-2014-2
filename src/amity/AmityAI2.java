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
 public double[] coefficients = {0.3873903606334963, 
 0.04585633721581077, 
 0.09445581478240499, 
 0.07473365052873632, 
 0.07482815637667657,
 0.11868667604426739, 
 0.4262318925468999, 
 0.3515108914987468, 
 0.34084080219176627, 
 0.09047581309278363, 
 0.27597446034724027, 
 0, //0.8614281749606392, 
 0.03335335871335531};

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
		int bestY = 0;.
		Piece bestPiece = piece;
		Piece current = piece;

		// loop through all the rotations
		do {
			final int yBound = limitHeight - current.getHeight() + 1;
			final int xBound = board.getWidth() - current.getWidth() + 1;

			// For current rotation, try all the possible columns
			for (int x = 0; x < xBound; x++) {
				int y = board.dropHeight(current, x);
				// piece does not stick up too far
				if ((y < yBound) && board.canPlace(current, x, y)) {
					Board testBoard = new Board(board);
					testBoard.place(current, x, y);
					testBoard.clearRows();

					double score = boardRater.rateBoard(testBoard);
					score = score + this.rate(testBoard);
					if (score < bestScore) {
						bestScore = score;
						bestX = x;
						bestY = y;
						bestPiece = current;
					}
				}
			}

			current = current.nextRotation();
		} while (current != piece);

		Move move = new Move();
		move.x = bestX;
		move.y = bestY;
		move.piece = bestPiece;
		return (move);	
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
		return average / w * 0.8614281749606392;
	}

	public void setRater(BoardRater r) {
		return; // does not support yet!
	}

	public String toString() {
		return "AmityAi2";
	}

}