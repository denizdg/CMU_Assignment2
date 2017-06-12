package bingo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Bingo {

	private Random rand = new Random();
	private int[][] card; // Bingo card configuration
	private int[] stream; // list of 75 integers
	private boolean[][] marks; // simulates placing chips on a Bingo card

	public Bingo() {
		card = new int[5][5];
		stream = new int[75];
		marks = new boolean[5][5];
	}

	/**
	 * This method writes a random Bingo card configuration and a stream of
	 * random number between 1 and 75 to the output file.
	 *
	 * The first column in the table contains only integers between 1 and 15,
	 * the second column numbers are all between 16 and 30, the third are 31 to
	 * 45, the fourth 46-60, and the fifth 61-75.
	 *
	 * There are no duplicate numbers on a Bingo card.
	 */
	public void write(String outputFile) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		int[][] bingoCard = new int[5][5];

		// for the first column - range 1-15
		bingoCard = generate(bingoCard, 1, 15, 0);
		// for the second column - range 16-30
		bingoCard = generate(bingoCard, 16, 30, 1);
		// for the third column - range 31-45
		bingoCard = generate(bingoCard, 31, 45, 2);
		// for the forth column - range 46-60
		bingoCard = generate(bingoCard, 46, 60, 3);
		// for the fifth column - range 61-75
		bingoCard = generate(bingoCard, 61, 75, 4);

		try {
			PrintWriter writer = new PrintWriter(outputFile);

			// print the bingo card to the file
			for (int i = 0; i < bingoCard.length; i++) {
				for (int j = 0; j < bingoCard.length; j++) {

					if (j == bingoCard.length - 1) {
						writer.print(bingoCard[j][i] + " ");
						writer.println();
						break;
					}

					writer.print(bingoCard[j][i] + " ");
				}
			}

			// print all the 75 numbers
			shuffle(numbers);

			for (Integer i : numbers) {

				writer.print(i + " ");

			}

			writer.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		}

	}

	public int[][] generate(int[][] bingoCard, int minRange, int maxRange, int columnNumber) {

		ArrayList<Integer> numbers = new ArrayList<Integer>();

		for (int i = 0; i < bingoCard.length; i++) {

			boolean generate = true;

			if (columnNumber == 2 && i == 2) {
				bingoCard[columnNumber][i] = 0;
				numbers.add(0);
				generate = false;
			}

			while (generate) {

				bingoCard[columnNumber][i] = rand.nextInt(maxRange - minRange + 1) + minRange;
				if (!numbers.contains(bingoCard[columnNumber][i])) {
					numbers.add(bingoCard[columnNumber][i]);
					break;
				}

				if (numbers.size() == bingoCard.length)
					generate = false;
			}
		}

		return bingoCard;
	}

	/**
	 * Shuffles the list of numbers
	 */
	public void shuffle(ArrayList<Integer> list) {
		list.add(rand.nextInt(75));

		boolean generate = true;

		while (generate) {
			int number = rand.nextInt(75) + 1;

			if (!list.contains(number)) {
				list.add(number);

			}

			if (list.size() == 75)
				generate = false;
		}
	}

	/**
	 * This method reads a given inputFile that contains a Bingo card
	 * configuration and a stream of numbers between 1 and 75. . A Bingo card
	 * configuration is stored in the card array. A list of 75 integers is
	 * stored in the stream array.
	 */
	public void read(String inputFile) {
		try {

			File file = new File(inputFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(fileReader);

			int lineCounter = 0;

			while (lineCounter != 5) {
				String line = buffer.readLine();
				String[] lineSplit = line.split(" ");

				for (int i = 0; i < lineSplit.length; i++) {
					card[lineCounter][i] = Integer.parseInt(lineSplit[i]);
				}

				lineCounter++;
			}

			if (lineCounter == 5) {
				String shuffleLine = buffer.readLine();
				String[] shuffleNumbers = shuffleLine.split(" ");

				for (int i = 0; i < shuffleNumbers.length; i++) {
					stream[i] = Integer.parseInt(shuffleNumbers[i]);
				}

			}

			buffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method returns the first integer from the stream array that gives
	 * you the earliest winning condition.
	 *
	 * - all the spots in a column are marked - all the spots in a row are
	 * marked - all the spots in either of the two diagonals are marked - all
	 * four corner squares are marked
	 */
	public int playGame() {
		int winner = 0;

		String[] combinations = new String[marks.length];
		String[] cornerCombinations = new String[4];

		combinations = arrangeCombinations();
		cornerCombinations = arrangeCornerCombinations();

		marks[2][2] = true;

		for (int i = 0; i < stream.length; i++) {
			if (stream[i] >= 1 && stream[i] <= 15)
				winner = mark(i, 0, combinations, cornerCombinations);
			else if (stream[i] >= 16 && stream[i] <= 30)
				winner = mark(i, 1, combinations, cornerCombinations);
			else if (stream[i] >= 31 && stream[i] <= 45)
				winner = mark(i, 2, combinations, cornerCombinations);
			else if (stream[i] >= 46 && stream[i] <= 60)
				winner = mark(i, 3, combinations, cornerCombinations);
			else if (stream[i] >= 61 && stream[i] <= 75)
				winner = mark(i, 4, combinations, cornerCombinations);

			if (winner != 0) {
				break;
			}
		}
		return winner;
	}

	// Check the matching range column and mark the array as true if card has the number
	public int mark(int streamIndex, int cardColumn, String[] combinations, String[] cornerCombinations) {

		int winner = 0;

		for (int j = 0; j < card.length; j++) {
			if (card[j][cardColumn] == stream[streamIndex]) {
				marks[j][cardColumn] = true;
				// Every time card has a number, check the possibility of winning
				winner = check(j, cardColumn, combinations, cornerCombinations);
				break;
			}
		}

		return winner;

	}

	public int check(int row, int column, String[] combinations, String[] cornerCombinations) {
		boolean isWin = false;

		int winnerRow = -1;
		int winnerColumn = -1;
		int winner = 0;

		boolean[] compareRow = new boolean[marks.length];
		boolean[] compareColumn = new boolean[marks.length];
		boolean[] compareDiagonalLeftToRight = new boolean[marks.length];
		boolean[] compareDiagonalRightToLeft = new boolean[marks.length];
		boolean[] compareCorners = new boolean[4];

		// Check the numbers in the same row
		for (int i = 0; i < marks.length; i++) {
			// Get all the values which are in the same row with the marked number
			compareRow[i] = marks[row][i];
											
		}

		// Check the numbers in the same column
		for (int i = 0; i < marks.length; i++) {
			// Get all the values which are in the same column with the marked number
			compareColumn[i] = marks[i][column];
		}

		if (row == column) {
			// If the marked number is in the diagonal line, get all the values on the diagonal line
			// From upper left corner to bottom right corner
			for (int i = 0; i < marks.length; i++) {
				compareDiagonalLeftToRight[i] = marks[i][i];
			}
		}

		if ((row > column && row % 2 == column) || (row < column && column % 2 == row)) {
			// If the marked number is in the reverse diagonal line, get all the values on that diagonal line
			// From bottom left corner to upper right corner
			for (int i = 0; i < marks.length; i++) {
				for (int j = 0; j < marks.length; j++) {
					if ((i + j) == marks.length - 1)
						compareDiagonalRightToLeft[i] = marks[j][i];
				}
			}
		}

		for (int i = 0; i < combinations.length; i++) {

			boolean[] boolComb = stringToBoolean(combinations[i]);

			//If a number is marked, every row and column which that number belong will be checked.

			// The index of the only false element in the compareRow array, is equal to the column value of the winning number in the card array.
			if (Arrays.equals(boolComb, compareRow)) {
				for (int j = 0; j < compareColumn.length; j++) {
					if (compareRow[j] == false) {
						winnerColumn = j;
						break;
					}
				}
				winner = card[row][winnerColumn];
				break;
			}

			// The index of the only false element in the compareColumn array, is equal to the row value of the winning number in the card array.
			if (Arrays.equals(boolComb, compareColumn)) {
				for (int j = 0; j < compareColumn.length; j++) {
					if (compareColumn[j] == false) {
						winnerRow = j;
						break;
					}
				}
				winner = card[winnerRow][column];
				break;
			}

			// If the marked number is on the both diagonal lines, do the following two comparisons just for these condition.
			if (row == column) {
				if (Arrays.equals(boolComb, compareDiagonalLeftToRight)) {
					for (int j = 0; j < compareDiagonalLeftToRight.length; j++) {
						if (compareDiagonalLeftToRight[j] == false) {
							winnerRow = j;
							break;
						}
					}
					winner = card[winnerRow][winnerRow];
					break;
				}
			}

			if ((row > column && row % 2 == column) || (row < column && column % 2 == row)) {
				if (Arrays.equals(boolComb, compareDiagonalRightToLeft)) {
					for (int j = 0; j < compareDiagonalRightToLeft.length; j++) {
						if (compareDiagonalRightToLeft[j] == false) {
							winnerColumn = j;
							winnerRow = (card.length - 1) - winnerColumn;
							break;
						}
					}
					winner = card[winnerRow][winnerColumn];
					break;
				}
			}

			// Check if the numbers are on the corners.
			if ((row == 0 && column == 0) || (row == 0 && column == marks.length - 1)
					|| (row == marks.length - 1 && column == 0)
					|| (row == marks.length - 1 && column == marks.length - 1)) {
				compareCorners[0] = marks[0][0];
				compareCorners[1] = marks[0][4];
				compareCorners[2] = marks[4][0];
				compareCorners[3] = marks[4][4];

				for (int k = 0; k < cornerCombinations.length; k++) {

					boolean[] boolCornerComb = stringToBoolean(cornerCombinations[k]);

					if (Arrays.equals(boolCornerComb, compareCorners)) {
						for (int p = 0; p < compareCorners.length; p++) {
							if (compareCorners[p] == false) {
								switch (p) {
								case 0:
									winner = card[0][0];
									break;
								case 1:
									winner = card[0][4];
									break;
								case 2:
									winner = card[4][0];
									break;
								case 3:
									winner = card[4][4];
									break;
								}
							}

						}
						break;
					}
				}

			}
		}

		return winner;

	}

	public boolean[] stringToBoolean(String str) {
		String[] strArr = str.split(",");
		boolean[] boolArr = new boolean[strArr.length];

		for (int j = 0; j < boolArr.length; j++) {
			boolArr[j] = Boolean.valueOf(strArr[j]);
		}

		return boolArr;
	}

	// Winning condition patterns.
	public String[] arrangeCombinations() {

		String[] combinations = new String[marks.length];

		combinations[0] = "true,true,true,true,false";
		combinations[1] = "true,true,true,false,true";
		combinations[2] = "true,true,false,true,true";
		combinations[3] = "true,false,true,true,true";
		combinations[4] = "false,true,true,true,true";

		return combinations;

	}

	// Winning condition patterns for corners.
	public String[] arrangeCornerCombinations() {

		String[] combinations = new String[4];

		combinations[0] = "true,true,true,false";
		combinations[1] = "true,true,false,true";
		combinations[2] = "true,false,true,true";
		combinations[3] = "false,true,true,true";

		return combinations;

	}

}
