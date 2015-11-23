/**
 * @author The2b
 * @date Nov 15, 2015
 * 
 * @purpose Runs a game of TicTacToe.
 * 	Asks for player info (Specifically how many real players and who goes first), builds them, then build a board.
 * 	Then, let the games begin.
 * 
 * @super Object
 */
package Assignment4;

import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game tictactoe = new Game();

		// Build players NOTE: Eventually make it so it asks about AI. For now, just assume p1 = real, p2 = ai
		Player pNull = new Player(0, " ", false);
		Player p1 = new Player(1, "X", false);
		Player p2 = new Player(2, "O", true);
		// Put the players in an array to make the print func work. Null as a placeholder for the "blank" player
		Player players[] =
		{
				pNull, p1, p2
		};

		// NOTE: Write stdout header

		// Welcome
		System.out.println("Welcome to Tic-Tac-Toe!");

		// Reset the game, just to be safe
		tictactoe.initGame();

		// And outsource the work to the Game object
		tictactoe.letsPlayAGame(players);

		tictactoe.cleanup(players);
	}
}

/**
 * @author The2b
 * @date Nov 15, 2015
 * 
 * @purpose Holds info & methods for the players. Due to project constraints, it currently forces the game pieces to be "X" and "O"
 * @super Object
 * 
 *        NOTE: I may want to add in a separate AI class, but adding more than 3 in one file is probably a bit overkill.
 *        As if 3 isn't too much already
 *        I'll just have to deal with it for now. Eventually he'll let me separate classes info files
 */

class Player {

	Random rand = new Random();

	int playerId = 0;
	String piece = "";
	boolean ai = false;
	Scanner in = new Scanner(System.in);

	public Player(int id, String mark, boolean isAi) {
		playerId = id;
		piece = mark;
		ai = isAi;
	}

	/**
	 * 
	 * @param x
	 *        The X coordinate to check
	 * @param y
	 *        The Y coordinate to check
	 * @param game
	 *        The game object we are using
	 * @return Whether or not the spot on the board is valid
	 */
	private boolean checkSpot(int x, int y, Game game) {
		if (game.board[y][x] != 0) {
			return false;
		}
		// If it goes this far, it obviously is not invalid, therefore valid
		return true;
	}

	// NOTE: Should crash if the player enters values > 3 or < 1
	// NOTE: He may want me to decompose these methods.
	// It may be annoying, but I may have to make separate makeTurnAI and a makeTurnPlayer methods

	/**
	 * Essentially holds 2 methods: 1 for the AI, 1 for a non-AI.
	 * 
	 * The AI method generates 2 random values,
	 * checks the spot using the checkSpot() method, and either re-generates numbers if the spot is taken,
	 * or sets the spot to the comp's ID if it's empty.
	 * 
	 * The non-AI method asks for an input spot in the format X, Y
	 * That string is then split, trimmed, parsed, and checked for validity using the checkSpot() method.
	 * If the spot is valid, we set it to the player's ID. If not, we complain about them, then try again
	 * until a valid spot is chosen.
	 * 
	 * @param game
	 *        The game info we are using
	 */
	public void makeTurn(Game game) {

		int xCoord = 0;
		int yCoord = 0;
		boolean isValid = false;

		if (ai) { // If it's an AI, let it do its thing on its own
			xCoord = 0;
			yCoord = 0;
			do {
				xCoord = rand.nextInt(3);
				yCoord = rand.nextInt(3);

				isValid = checkSpot(xCoord, yCoord, game);

			} while (!isValid);
			// The random values should be valid by this point, so we can set it
			game.board[yCoord][xCoord] = playerId;
			System.out.println("The Computer Chose: " + (xCoord  + 1) + ", " + (yCoord + 1)); // Add +1 to display since board starts at 1, but rng starts at 0
		}
		else { // If it's not, we need to ask the player where they want to go
			do {
				System.out.print("Choose your spot (X,Y): ");
				// Take the raw string, split & cook it, then parse it
				String input = in.nextLine();
				String[] rawSplit = input.split(",");

				xCoord = Integer.parseInt(rawSplit[0].trim());
				yCoord = Integer.parseInt(rawSplit[1].trim());

				// Check the values. If they are valid, set it. If not, complain and try again
				if (checkSpot(xCoord - 1, yCoord - 1, game)) {
					isValid = true;
					game.board[yCoord - 1][xCoord - 1] = playerId;
				}
				else {
					System.out.println("Error! That spot is taken!");
				}
			} while (!isValid);
		}
	}
}

/**
 * @author The2b
 * @date Nov 15, 2015
 * 
 * @purpose Holds info for the active game info. Turn count, board status, etc
 * @super Object
 */

class Game {

	public int[][] board =
	{
			{
					0, 0, 0
			},
			{
					0, 0, 0
			},
			{
					0, 0, 0
			}
	};

	private int turnCount = 0;

	/**
	 * Resets all the values of the game's board, and the turn count. Effectively re-starts the game
	 */
	public void initGame() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[y][x] = 0;
			}
		}
		turnCount = 0;
	}

	/**
	 * 
	 * So, in TicTacToe, there are 8 ways to win. There is a visual in the Notes folder to follow along.
	 * 
	 * Rows: Top, Mid Bottom
	 * Columns: Left, Mid, Right
	 * Diagonals: Top Left to Bottom Right, Top Right to Bottom Left
	 * 
	 * Since no one can win until the 5th turn at the earliest, we can just return 0 until then
	 * 
	 * I could write a more complex, but more efficient code to only check what's relevant, such as only checking the mid column and top row
	 * if (X,Y) (1,0) is selected, but since it'll only be run 4 times a game max, and this is long enough already, I'll trade efficiency
	 * for a smaller file
	 * 
	 * @return The ID of the winner. If the ID is 0, we ignore it, since that means there is no winner
	 * 
	 *         NOTE!!!!: This is ***NOT*** run implicitly every go, although that may change. For now, we must run it every time explicitly.
	 */
	public int checkWin() {

		if (turnCount < 5) {
			return 0;
		} // If it's higher than that, we just continue

		// Check rows
		for (int y = 0; y < 3; y++) {
			if (board[y][1] == board[y][0] && board[y][1] == board[y][2]) {
				return board[y][1];
			}
		}

		// Check columns
		for (int x = 0; x < 3; x++) {
			if (board[1][x] == board[0][x] && board[1][x] == board[2][x]) {
				return board[1][x];
			}
		}

		// Top Left to Bottom Right is easy. I can just check board[coord][coord], since they will always be the same for that diag
		// Top Right to Bottom Left is harder. I'll need to check board[y][2-y]
		// Regardless, I'll need to check them manually, as far as I can tell. At least, that's the easiest way to do it
		if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
			return board[1][1];
		}
		if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) { // no need for elif since the returns effectively does that for me
			return board[1][1];
		}

		// If it's turn 9, call it a draw
		if (turnCount >= 9) {
			return -1; // -1 is a draw
		}

		return 0; // Same reason for no else as above
	}

	/**
	 * Print the board with player piece strings, gotten from the player info array
	 * @param playerData
	 *        Player info array
	 */
	public void printBoard(Player[] playerData) { // Totally not overly convoluted at all \s. But I already did it, so whatever
		System.out.println();
		System.out.println(" 1 2 3 ");
		System.out.println("--------");
		System.out.printf("1|%s|%s|%s|\n", playerData[board[0][0]].piece, playerData[board[0][1]].piece, playerData[board[0][2]].piece);
		System.out.println("--------");
		System.out.printf("2|%s|%s|%s|\n", playerData[board[1][0]].piece, playerData[board[1][1]].piece, playerData[board[1][2]].piece);
		System.out.println("--------");
		System.out.printf("3|%s|%s|%s|\n", playerData[board[2][0]].piece, playerData[board[2][1]].piece, playerData[board[2][2]].piece);
		System.out.println("--------");
		System.out.println();
	}

	// NOTE: Does not print a winner. Need to later write a check method and implement it
	// NOTE: Same for ask again

	/**
	 * Direct and supervise most of the actual work.
	 * @param players
	 *        Player info array
	 */
	public void letsPlayAGame(Player[] players) {
		int winner = 0;
		printBoard(players);
		do {
			players[1].makeTurn(this);
			turnCount++;
			printBoard(players);
			winner = checkWin();
			players[2].makeTurn(this);
			turnCount++;
			printBoard(players);
			winner = checkWin();
		} while (winner == 0);
	}

	/**
	 * Cleanup Scanners
	 * @param players
	 *        The array of players/playerinfo
	 */
	public void cleanup(Player[] players) {
		for (int c = 0; c < players.length; c++) {
			players[c].in.close();
		}
	}
}