package sukoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Sudoku {

	private int[][] board;
	private boolean invalidMove = false;
	private String initialCoordinate;
	private boolean solved = false;

	public static void main(String[] args) throws Exception {

		Sudoku game = new Sudoku();

		System.out.println("1 to generate a puzzle\n2 to solve a puzzle");

		Scanner scan = new Scanner(System.in);
		int i = scan.nextInt();
		scan.close();
		if (i==1) {
			game.generateInitialRow();
			game.importPuzzle();
			game.solve();// after calling solve(), set the solved flag to false if i wanna solve another puzzle again (like in a loop or something)
			game.filterSolution();
			game.importPuzzle();
			game.saveBoard();
		}
		if (i==2) {
			game.importPuzzle();
			game.solve();
			game.saveBoard();
		}
		System.out.println("/End/");

	}

	private void filterSolution() throws Exception {

		Random rand = new Random();
		ArrayList<String> keep = new ArrayList<String>();
		keep.clear();
		String randomNum;

		while(keep.size()<20) {
			randomNum = String.valueOf(rand.nextInt(89));

			if(randomNum.length() == 1) randomNum = "0" + randomNum;
			if(randomNum.charAt(1) == '9') continue;
			if(keep.contains(randomNum)) continue;

			keep.add(randomNum);
		}

		PrintWriter out = new PrintWriter("coordinates.txt");
		for (String x:keep) {
			int yAxis = Integer.parseInt(String.valueOf(x.charAt(0)));
			int xAxis = Integer.parseInt(String.valueOf(x.charAt(1)));
			out.println(x+board[yAxis][xAxis]);
		}
		out.close();

	}

	private void generateInitialRow() throws Exception {

		Random rand = new Random();
		ArrayList<String> generated = new ArrayList<String>();
		generated.clear();
		String randomNum;
		PrintWriter out = new PrintWriter("coordinates.txt");
		int i=0;
		while(generated.size()<9) {
			randomNum = String.valueOf(rand.nextInt(9)+1);
			if(generated.contains(randomNum)) continue;
			generated.add(randomNum);
			out.println("0" + i + randomNum);
			i++;
		}
		out.close();

	}

	public void importPuzzle() throws Exception{

		try(BufferedReader br = new BufferedReader(new FileReader("coordinates.txt"))) {
			clearBoard();
			String line = br.readLine();

			while (line != null) {
				parse(line);
				line = br.readLine();
			}
		}
	}

	public void solve() throws Exception {
		initialCoordinate = emptyCoordinate(); 
		solve(initialCoordinate);
	}

	private String solve(String coordinates) throws Exception {

		while(!solved){
			int yAxis = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
			int xAxis = Integer.parseInt(String.valueOf(coordinates.charAt(1)));

			if(invalidMove){
				invalidMove = false;
				return null;
			}

			if (board[yAxis][xAxis] == 9) {
				if(coordinates.equals(initialCoordinate)) System.out.println("No solutions found.");
				board[yAxis][xAxis] = 0;
				return null;
			}

			board[yAxis][xAxis] = 1 + board[yAxis][xAxis];

			if(!validMove(coordinates)){
				invalidMove = true;
			}

			String empties = emptyCoordinate();
			if (empties.equals("99") && !invalidMove) {
				solved = true;
				return null;
			}

			solve(empties);
		}
		return null;
	}

	private String emptyCoordinate() {

		for (int i=0;i<9;i++){
			for (int j=0;j<9;j++){
				if(board[i][j] == 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(i);
					sb.append(j);
					return sb.toString();
				}
			}
		}
		return "99";
	}

	private boolean validMove(String coordinates){
		return (validHorizontal(coordinates) && validVertical(coordinates) && validBlock(coordinates));
	}

	private boolean validBlock(String coordinates) {
		int yAxis = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
		int xAxis = Integer.parseInt(String.valueOf(coordinates.charAt(1)));

		int yBlock = yAxis/3;
		int xBlock = xAxis/3;

		int count=0;
		for (int i=(3*yBlock);i<(3*yBlock+3);i++){
			for (int j=(3*xBlock);j<(3*xBlock+3);j++){
				if(board[i][j] == board[yAxis][xAxis]) count++;
			}
		}

		return (count<2);
	}

	private boolean validVertical(String coordinates) {
		int yAxis = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
		int xAxis = Integer.parseInt(String.valueOf(coordinates.charAt(1)));

		int count=0;
		for (int i=0;i<9;i++){
			if(board[i][xAxis] == board[yAxis][xAxis]) count++;
		}
		return (count<2);
	}

	private boolean validHorizontal(String coordinates) {
		int yAxis = Integer.parseInt(String.valueOf(coordinates.charAt(0)));
		int xAxis = Integer.parseInt(String.valueOf(coordinates.charAt(1)));

		int count=0;
		for (int j=0;j<9;j++){
			if(board[yAxis][j] == board[yAxis][xAxis]) count++;
		}
		return (count<2);
	}

	private  void clearBoard() {
		board = new int[9][9];
		for (int i=0;i<9;i++){
			for (int j=0;j<9;j++){
				board[i][j] = 0;
			}
		}
	}

	private  void saveBoard() throws Exception {
		PrintWriter out = new PrintWriter("board.txt");
		for (int i=0;i<9;i++){
			for (int j=0;j<9;j++){
				out.print(board[i][j] + " ");
			}
			out.println();
		}
		out.close();
	}

	private void parse(String line) {
		int yAxis = Integer.parseInt(String.valueOf(line.charAt(0)));
		int xAxis = Integer.parseInt(String.valueOf(line.charAt(1)));
		int value = Integer.parseInt(String.valueOf(line.charAt(2)));

		board[yAxis][xAxis] = value;
	}
}
