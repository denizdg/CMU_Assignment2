package bingo;

import java.io.IOException;

public class MainDriver {

	public static void main (String[] args)
	   {
			Bingo game = new Bingo();
			game.write("input.txt");
			game.read("input.txt");

			int x = game.playGame();
			System.out.println("the winning number is " + x);
		}

}
