package amity;

import tetris.ITLPAI;
import tetris.RunTetris;

/**
 * 
 * @author drdanielfc
 *
 */
public class Main
{
	public static void main(String args[])
	{
		RunTetris.load(new ITLPAI());
		// RunTetris.load(new AmityAI());
	}

}